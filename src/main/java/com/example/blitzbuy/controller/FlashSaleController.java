package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.FlashSaleOrder;
import com.example.blitzbuy.pojo.FlashSaleMessage;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.service.FlashSaleOrderService;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.vo.GoodsVo;
import com.example.blitzbuy.vo.RespBeanEnum;
import com.google.code.kaptcha.Producer;
import com.example.blitzbuy.vo.RespBean;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.rabbitmq.MQSender;

import cn.hutool.json.JSONUtil;

/**
 * version 5.0
 * Flash Sale Controller: Handle user flash sale requests, return flash sale results
 * 1. Use Redis to pre-reduce inventory, solve high concurrency problems (version 3.0)
 * 2. Add local JVM memory check before pre-reducing inventory in Redis, reduce unnecessary Redis operations (version 3.0)
 * 3. Use message queue (RabbitMQ) to implement asynchronous processing of flash sale requests (version 4.0)
 * 4. Update "/doFlashSale" to "/doFlashSale/{path}" : add unique flash sale path for security check (version 5.0)
 * 5. Add captcha check to "/getFlashSalePath", avoid malicious requests (version 5.0)
 * 
 */

@Controller
@RequestMapping("/flashSale")
public class FlashSaleController implements InitializingBean {

    @Resource
    private GoodsService goodsService;

    @Resource
    private FlashSaleOrderService flashSaleOrderService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MQSender mqSender;

    @Resource
    private Producer captchaProducer;

    //Define a map to store the goods stock in local JVM memory, key: goodsId, value: hasStock
    private Map<Long, Boolean> goodsStockMap = new HashMap<>();

   

    /**
     * Do flash sale: Handle user flash sale request
     * @param path flash sale path
     * @param model model
     * @param user current user
     * @param goodsId goods id
     * @return flash sale result
     */
    @RequestMapping("/doFlashSale/{path}")
    @ResponseBody
    public RespBean doFlashSale(@PathVariable String path, Model model, User user, Long goodsId){

        // 0-1. Check if user is logged in
        if( user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
       
        // 0-2. Check if the path is valid
        if(!orderService.checkFlashSalePath(user, goodsId, path)){
            // If the path is invalid, return request illegal error
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        // 0-3. Get goodsVo(database)
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        
        // Flash sale logic
        // 1. Query promotional goods inventory(database)
        int stock = goodsVo.getFlashSaleStock();
        if(stock <= 0){ 
            // If the inventory is less than or equal to 0, return no stock error
            return RespBean.error(RespBeanEnum.NO_STOCK);
        }


        // 2. Check if user is repurchasing goods (Redis)
        FlashSaleOrder flashSaleOrder = (FlashSaleOrder)redisTemplate.opsForValue().get("flashSaleOrder:" + user.getId() + ":" + goodsId);
        if(flashSaleOrder != null){ 
            // If the flash sale order exists in Redis, user has already successfully purchased
            // Return repeat purchase error
            return RespBean.error(RespBeanEnum.REPEAT_PURCHASE);
        }


        // 3. Optimization: Check if the inventory is marked as false in local JVM memory
        Boolean hasStock = goodsStockMap.get(goodsId);
        if(hasStock == null || !hasStock){
            // If the inventory is marked as false in local JVM memory, return no stock error
            return RespBean.error(RespBeanEnum.NO_STOCK);
        }

        // 4. Optimization: Pre-reduce inventory in Redis
        // If the inventory is less than 0 in Redis, return flash sale failed
        // Thus, reduce the number of calls to orderService.creatFlashSaleOrder(), avoid crashing the database, and prevent thread accumulation
        // Method: decrement() is atomic, can avoid concurrency problems
        // return the decremented stock
        
        // Check if Redis stock exists, if not, initialize it from database
        String redisStockKey = "flashSaleStock:" + goodsId;
        if (!redisTemplate.hasKey(redisStockKey)) {
            redisTemplate.opsForValue().set(redisStockKey, goodsVo.getFlashSaleStock());
            // Also update local memory
            goodsStockMap.put(goodsId, goodsVo.getFlashSaleStock() > 0);
        }
        // Decrement the stock in Redis
        Long decrementedStock = redisTemplate.opsForValue().decrement(redisStockKey); 
        
        // If the inventory is less than 0
        if(decrementedStock < 0){ 
           
            // Set the goods stock to false in local JVM memory
            goodsStockMap.put(goodsId, false);

            // Set the goods stock back to 0 in Redis
            redisTemplate.opsForValue().set(redisStockKey, 0);

            // Return no stock error
            return RespBean.error(RespBeanEnum.NO_STOCK);
        }

        // 5. If not, do flash sale order asynchronously
        // 5.1 Send flash sale message to RabbitMQ, MQReceiver will call orderService.creatFlashSaleOrder() asynchronously
        // 5.1.1 Create flash sale message
        FlashSaleMessage flashSaleMessage = new FlashSaleMessage(user, goodsId);
        // 5.1.2 Convert flash sale message to JSON string
        String message = JSONUtil.toJsonStr(flashSaleMessage);
        // 5.1.3 Send flash sale message to RabbitMQ
        mqSender.sendFlashSaleMessage(message);
        
        // 5.1.4 Mark message as sent for tracking
        redisTemplate.opsForValue().set("flashSaleMessageSent:" + user.getId() + ":" + goodsId, "1", 30, TimeUnit.SECONDS);

        // 5.2 Set goodsId for polling
        model.addAttribute("goodsId", goodsId);

        // 5.3 Return in queue message
        return RespBean.error(RespBeanEnum.IN_QUEUE);

    }

    /**
     * Get flash sale result for polling
     * @param user current user
     * @param goodsId goods id
     * @return RespBean with result from OrderService
     */
    @RequestMapping("/getFlashSaleResult")
    @ResponseBody
    public RespBean getFlashSaleResult(User user, @RequestParam Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.ERROR);
        }

        // Add rate limiting - check if user is polling too frequently
        String rateLimitKey = "rateLimit:" + user.getId() + ":" + goodsId;
        String lastPollTime = (String) redisTemplate.opsForValue().get(rateLimitKey);
        long currentTime = System.currentTimeMillis();
        
        if (lastPollTime != null) {
            long timeDiff = currentTime - Long.parseLong(lastPollTime);
            // Limit polling to once every 50ms
            if (timeDiff < 50) {
                return RespBean.error(RespBeanEnum.ERROR);
            }
        }
        
        // Update last poll time
        redisTemplate.opsForValue().set(rateLimitKey, String.valueOf(currentTime), 60, TimeUnit.SECONDS);

        Long result = orderService.getFlashSaleResult(user.getId(), goodsId);
        return RespBean.success(result);
    }



    @RequestMapping("/getFlashSalePath")
    @ResponseBody
    public RespBean getFlashSalePath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if(user == null || goodsId == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }


        // check if the captcha is valid
        if(!orderService.checkCaptcha(user, goodsId, captcha)){
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        // Create a unique path for flash sale request
        String path = orderService.createFlashSalePath(user, goodsId);
        // Return the path
        return RespBean.success(path);
    }

    // create kaptcha
    @RequestMapping("/getCaptcha")
    @ResponseBody
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response, User user, Long goodsId){
        try {
            // set response header
            response.setContentType("image/jpeg");
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            
            // create captcha text and image
            String capText = captchaProducer.createText();
            BufferedImage bi = captchaProducer.createImage(capText);

            // Kaptcha will store captcha to session automatically, key: "KAPTCHA_SESSION_KEY" 

            // also store captcha to Redis, key:captcha:userId:goodsId, value:captchaText
            redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, capText, 100, TimeUnit.SECONDS);
            
            // output captcha image
            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
            out.flush();
            out.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("Captcha generation failed");
            } catch (Exception ex) {
                // ignore write error
            }
        }
    }

    /**
     * Initialize flash sale stock in Redis
     * （This method is executed after all properties of the class are initialized）
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // Query all flash sale goods
        List<GoodsVo> goodsVoList = goodsService.getGoodsVo();
        // If the flash sale goods list is empty, return
        if(goodsVoList == null || goodsVoList.isEmpty()){
            return;
        }
        for(GoodsVo goodsVo : goodsVoList){
            
            // 1.Set flash sale stock to Redis(key: flashSaleStock:{goodsId}, value: flashSaleStock)
            redisTemplate.opsForValue().set("flashSaleStock:" + goodsVo.getId(), goodsVo.getFlashSaleStock());

            // 2. Set flash sale stock to local JVM memory
            // Initialize the goods stock map
            // If goodsId : false -> empty stock
            // If goodsId : true -> has stock
            goodsStockMap.put(goodsVo.getId(), true);
        }
    }

}
