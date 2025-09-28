package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.FlashSaleOrder;
import com.example.blitzbuy.pojo.Order;
import com.example.blitzbuy.config.AccessLimit;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.service.FlashSaleOrderService;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.util.UUIDUtil;
import com.example.blitzbuy.vo.GoodsVo;
import com.example.blitzbuy.vo.RespBeanEnum;
import com.google.code.kaptcha.Producer;
import com.example.blitzbuy.vo.RespBean;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.example.blitzbuy.pojo.User;


/**
 * version 7.0
 * Flash Sale Controller: Handle user flash sale requests, return flash sale results
 * 1. Use Redis to pre-reduce inventory, solve high concurrency problems (version 3.0)
 * 2. Add local JVM memory check before pre-reducing inventory in Redis, reduce unnecessary Redis operations (version 3.0)
 * 3. Use message queue (RabbitMQ) to implement asynchronous processing of flash sale requests (version 4.0)
 * 4. Update "/doFlashSale" to "/doFlashSale/{path}" : add unique flash sale path for security check (version 5.0)
 * 5. Add captcha check to "/getFlashSalePath", avoid malicious requests (version 5.0)
 * 6. Add AccessLimit annotation to "/getFlashSalePath", avoid malicious requests (version 6.0)
 * 7. Update Mysql row-level lock to Redis distributed lock, ensure data consistency under more complex business logic or distributed deployment (version 7.0)
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
    private Producer captchaProducer;

    @Resource
    private RedisScript<Long> redisScript;


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
       
        // 0-2. Check if the path is valid (skip for load testing)
        boolean pathValid;
        if ("LOAD_TEST".equals(path)) {
            pathValid = true;
        } else {
            pathValid = orderService.checkFlashSalePath(user, goodsId, path);
        }
        
        if(!pathValid){
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
        // String redisStockKey = "flashSaleStock:" + goodsId;
        // if (!redisTemplate.hasKey(redisStockKey)) {
        //     redisTemplate.opsForValue().set(redisStockKey, goodsVo.getFlashSaleStock());
        //     // Also update local memory
        //     goodsStockMap.put(goodsId, goodsVo.getFlashSaleStock() > 0);
        // }
        // // Decrement the stock in Redis
        // Long decrementedStock = redisTemplate.opsForValue().decrement(redisStockKey); 
        
        // // If the inventory is less than 0
        // if(decrementedStock < 0){ 
           
        //     // Set the goods stock to false in local JVM memory
        //     goodsStockMap.put(goodsId, false);

        //     // Set the goods stock back to 0 in Redis
        //     redisTemplate.opsForValue().set(redisStockKey, 0);

        //     // Return no stock error
        //     return RespBean.error(RespBeanEnum.NO_STOCK);
        // }

        //  4. 优化Mysql行锁方案：改用Redis分布式锁，解决分布式部署下的数据一致性问题
        // 虽然decrement()已经足够处理本项目在单机环境下的业务需求，但如果考虑到分布式部署、多方服务调用、复杂业务逻辑等因素，就需要进一步扩大隔离性的范围
        // 分布式锁的实现方式：使用Redis的setnx命令，对应setIfAbsent()方法
        // key: lock:goodsId，value: 随机生成一个UUID，作为锁的值
        String lockKey = "lock:" + goodsId;
        String uuid = UUIDUtil.uuid();
        // 如果key不存在，则设置key的值为value，并返回true，如果key存在，则返回false 
        // 优化：将锁超时时间从3秒延长到5秒，提高高并发下的成功率
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 5, TimeUnit.SECONDS);
        if(!lock){
            // 如果锁已存在，获取锁失败，尝试重试一次
            try {
                Thread.sleep(10); // 等待10毫秒后重试，优化等待时间
                lock = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 5, TimeUnit.SECONDS);
                if(!lock){
                    // 重试后仍然失败，返回重试错误
                    return RespBean.error(RespBeanEnum.TRY_AGAIN);
                }
            } catch (InterruptedException e) {
                // 线程被中断，返回重试错误
                return RespBean.error(RespBeanEnum.TRY_AGAIN);
            }
        }

        // 如果锁不存在，获取锁成功，可以进行多项业务操作(此处仅需预减库存-1)
        String redisStockKey = "flashSaleStock:" + goodsId;
        Long decrementedStock = redisTemplate.opsForValue().decrement(redisStockKey);
        if(decrementedStock < 0){ // If the inventory is less than 0
            // Set the goods stock to false in local JVM memory
            goodsStockMap.put(goodsId, false);
            // Set the goods stock back to 0 in Redis
            redisTemplate.opsForValue().set(redisStockKey, 0);
            // Release the lock by LUA script
            redisTemplate.execute(redisScript, Arrays.asList(lockKey), uuid);
            // Return no stock error
            return RespBean.error(RespBeanEnum.NO_STOCK);
        
        }
        // If the inventory is greater or equal to 0,still need to release the lock
        redisTemplate.execute(redisScript, Arrays.asList(lockKey), uuid);


        // 5. Create flash sale order directly (synchronous)
        try {
            Order order = orderService.creatFlashSaleOrder(user, goodsVo);
            
            if (order != null) {
                // Flash sale successful, return order ID
                return RespBean.success(order.getId());
            } else {
                // Flash sale failed
                return RespBean.error(RespBeanEnum.NO_STOCK);
            }
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Flash sale error: " + e.getMessage());
            e.printStackTrace();
            
            // Rollback Redis stock if needed
            redisTemplate.opsForValue().increment("flashSaleStock:" + goodsId);
            
            return RespBean.error(RespBeanEnum.ERROR);
        }

    }




    @RequestMapping("/getFlashSalePath")
    @ResponseBody
    @AccessLimit(seconds = 5, maxCount = 5)
    public RespBean getFlashSalePath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if(user == null || goodsId == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }


        // check if the captcha is valid
        if(!orderService.checkCaptcha(user, goodsId, captcha)){
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        
        System.out.println("=== GET FLASH SALE PATH ===");
        System.out.println("User ID: " + user.getId());
        System.out.println("Goods ID: " + goodsId);
        
        // Create a unique path for flash sale request
        String path = orderService.createFlashSalePath(user, goodsId);
        System.out.println("Generated path: " + path);
        System.out.println("Returning path to frontend");
        System.out.println("==========================");
        
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
