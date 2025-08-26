package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.FlashSaleOrder;
import com.example.blitzbuy.pojo.FlashSaleMessage;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.service.FlashSaleOrderService;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.vo.GoodsVo;
import com.example.blitzbuy.vo.RespBeanEnum;
import com.example.blitzbuy.vo.RespBean;
import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.rabbitmq.MQSender;

import cn.hutool.json.JSONUtil;

/**
 * version 4.0
 * Flash Sale Controller: Handle user flash sale requests, return flash sale results
 * 1. Use Redis to pre-reduce inventory, solve high concurrency problems (version 3.0)
 * 2. Add local JVM memory check before pre-reducing inventory in Redis, reduce unnecessary Redis operations (version 3.0)
 * 3. Use message queue (RabbitMQ) to implement asynchronous processing of flash sale requests (version 4.0)
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

    //Define a map to store the goods stock in local JVM memory, key: goodsId, value: hasStock
    private Map<Long, Boolean> goodsStockMap = new HashMap<>();

   

    // Handle user flash sale request
    @RequestMapping("/doFlashSale")
    public String doFlashSale(Model model, User user, Long goodsId){

        // 0. Check if user is logged in
        if( user == null){
            return "login";
        }
        model.addAttribute("user", user);

        // Get goodsVo
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        
        // Flash sale logic

        // 1. Query promotional goods inventory(database)
        int stock = goodsVo.getFlashSaleStock();
        if(stock <= 0){ 
            // If the inventory is less than or equal to 0, return flash sale failed
            model.addAttribute("msg", RespBeanEnum.INSUFFICIENT_STOCK.getMessage());
            return "flashSaleFail";
        }


        // 2. Check if user is repurchasing goods (check if the flash sale order exists in Redis)
        FlashSaleOrder flashSaleOrder = (FlashSaleOrder)redisTemplate.opsForValue().get("flashSaleOrder:" + user.getId() + ":" + goodsId);
        if(flashSaleOrder != null){ 
            // If the flash sale order exists in Redis, user has already successfully purchased
            // Set a special flag to indicate already purchased
            model.addAttribute("alreadyPurchased", true);
            model.addAttribute("orderId", flashSaleOrder.getOrderId());
            model.addAttribute("goodsId", goodsId);
            return "flashSaleResult";
        }


        // 3. Optimization: Check if the inventory is marked as false in local JVM memory
        Boolean hasStock = goodsStockMap.get(goodsId);
        if(hasStock == null || !hasStock){
            // If the inventory is marked as false in local JVM memory, return flash sale failed
            model.addAttribute("msg", RespBeanEnum.INSUFFICIENT_STOCK.getMessage());
            return "flashSaleFail";
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
        
        Long decrementedStock = redisTemplate.opsForValue().decrement(redisStockKey); 
        
        // If the inventory is less than 0
        if(decrementedStock < 0){ 
           
            // set the goods stock to false
            goodsStockMap.put(goodsId, false);

            // return flash sale failed
            model.addAttribute("msg", RespBeanEnum.INSUFFICIENT_STOCK.getMessage());
            return "flashSaleFail";
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

        // 5.3 Client can query order status by polling
        return "flashSaleResult";

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
