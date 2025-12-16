package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.*;
import com.example.blitzbuy.rabbitmq.MQSender;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.util.UUIDUtil;
import com.example.blitzbuy.vo.GoodsVo;
import com.example.blitzbuy.vo.RespBean;
import com.example.blitzbuy.vo.RespBeanEnum;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Flash Sale API Controller
 * RESTful API endpoints for flash sale operations
 * 
 * @author Heather
 * @version 1.0
 * 
 * Features:
 * - Redis distributed lock for high concurrency
 * - Local JVM memory optimization
 * - Redis stock pre-decrement
 * - Complete error handling and rollback
 */
@RestController
@RequestMapping("/api/v1/flash-sale")
public class FlashSaleApiController implements InitializingBean {

    @Resource
    private OrderService orderService;

    @Resource
    private GoodsService goodsService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedisScript<Long> redisScript;

    @Resource
    private MQSender mqSender;

    // Local JVM memory cache for goods stock status
    // Key: goodsId, Value: hasStock (true/false)
    private Map<Long, Boolean> goodsStockMap = new HashMap<>();

    /**
     * Get captcha image URL
     * GET /api/v1/flash-sale/captcha/{goodsId}
     */
    @GetMapping("/captcha/{goodsId}")
    public RespBean getCaptchaUrl(@PathVariable Long goodsId) {
        String captchaUrl = "/flashSale/getCaptcha?goodsId=" + goodsId;
        return RespBean.success(captchaUrl);
    }

    /**
     * Get flash sale path after captcha verification
     * POST /api/v1/flash-sale/path
     */
    @PostMapping("/path")
    public RespBean getFlashSalePath(@RequestParam Long goodsId,
                                   @RequestParam String captcha,
                                   User user,
                                   HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Check captcha
        if (!orderService.checkCaptcha(user, goodsId, captcha)) {
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }

        // Generate flash sale path
        String path = orderService.createFlashSalePath(user, goodsId);
        return RespBean.success(path);
    }

    /**
     * Get flash sale path for load testing (skip captcha verification)
     * GET /api/v1/flash-sale/path/load-test
     * 
     * This endpoint is specifically for JMeter load testing.
     * It skips captcha verification and directly generates a path.
     */
    @GetMapping("/path/load-test")
    public RespBean getFlashSalePathForLoadTest(@RequestParam Long goodsId,
                                                User user,
                                                HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Generate flash sale path directly (skip captcha for load testing)
        String path = orderService.createFlashSalePath(user, goodsId);
        return RespBean.success(path);
    }

    /**
     * Check if user has already purchased this flash sale item
     * GET /api/v1/flash-sale/check-purchase/{goodsId}
     */
    @GetMapping("/check-purchase/{goodsId}")
    public RespBean checkPurchase(@PathVariable Long goodsId,
                                 User user,
                                 HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Check if user has already purchased this item
        FlashSaleOrder flashSaleOrder = 
            (FlashSaleOrder) redisTemplate.opsForValue()
                .get("flashSaleOrder:" + user.getId() + ":" + goodsId);
        
        if (flashSaleOrder != null) {
            return RespBean.error(RespBeanEnum.REPEAT_PURCHASE);
        }
        
        return RespBean.success("User can purchase this item");
    }

    /**
     * Execute flash sale purchase
     * POST /api/v1/flash-sale/purchase
     * 
     * Complete flash sale logic with Redis distributed lock:
     * 1. User authentication check
     * 2. Path validation
     * 3. Stock check (database)
     * 4. Repeat purchase check (Redis)
     * 5. Local JVM memory optimization
     * 6. Redis distributed lock
     * 7. Redis stock pre-decrement
     * 8. Create order
     * 9. Error handling and rollback
     */
    @PostMapping("/purchase")
    public RespBean purchase(@RequestParam String path,
                           @RequestParam Long goodsId,
                           User user,
                           HttpServletResponse response) {
        // 0-1. Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // 0-2. Check if the path is valid (skip for load testing)
        boolean pathValid;
        if ("LOAD_TEST".equals(path)) {
            pathValid = true;
        } else {
            pathValid = orderService.checkFlashSalePath(user, goodsId, path);
        }

        if (!pathValid) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        // 0-3. Get goodsVo (database)
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if (goodsVo == null) {
            return RespBean.error(RespBeanEnum.GOODS_NOT_EXIST);
        }

        // Flash sale logic
        // 1. Query promotional goods inventory (database)
        int stock = goodsVo.getFlashSaleStock();
        if (stock <= 0) {
            return RespBean.error(RespBeanEnum.NO_STOCK);
        }

        // 2. Check if user is repurchasing goods (Redis)
        FlashSaleOrder flashSaleOrder = (FlashSaleOrder) redisTemplate.opsForValue()
                .get("flashSaleOrder:" + user.getId() + ":" + goodsId);
        if (flashSaleOrder != null) {
            return RespBean.error(RespBeanEnum.REPEAT_PURCHASE);
        }

        // 2.1. Check if previous request failed (prevent repeated failed requests in loop testing)
        // This prevents users from repeatedly requesting after a failed purchase attempt
        String failKey = "flashSaleFail:" + user.getId() + ":" + goodsId;
        if (redisTemplate.hasKey(failKey)) {
            // Previous request failed, return no stock error to prevent repeated requests
            return RespBean.error(RespBeanEnum.NO_STOCK);
        }

        // 3. Optimization: Check if the inventory is marked as false in local JVM memory
        Boolean hasStock = goodsStockMap.get(goodsId);
        if (hasStock == null || !hasStock) {
            return RespBean.error(RespBeanEnum.NO_STOCK);
        }

        // 4. Redis distributed lock: Use Redis setnx command to ensure atomicity
        // Key: lock:goodsId, Value: randomly generated UUID as lock value
        String lockKey = "lock:" + goodsId;
        String uuid = UUIDUtil.uuid();
        // Set lock with 5 seconds timeout, retry once if failed
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 5, TimeUnit.SECONDS);
        if (!lock) {
            // If lock already exists, lock acquisition failed, try retry once
            try {
                Thread.sleep(10); // Wait 10 milliseconds before retry
                lock = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 5, TimeUnit.SECONDS);
                if (!lock) {
                    // Still failed after retry, return retry error
                    return RespBean.error(RespBeanEnum.TRY_AGAIN);
                }
            } catch (InterruptedException e) {
                // Thread interrupted, return retry error
                return RespBean.error(RespBeanEnum.TRY_AGAIN);
            }
        }

        try {
            // 5. Pre-reduce inventory in Redis (atomic operation)
            String redisStockKey = "flashSaleStock:" + goodsId;
            Long decrementedStock = redisTemplate.opsForValue().decrement(redisStockKey);

            if (decrementedStock < 0) {
                // If the inventory is less than 0
                // Set the goods stock to false in local JVM memory
                goodsStockMap.put(goodsId, false);
                // Set the goods stock back to 0 in Redis
                redisTemplate.opsForValue().set(redisStockKey, 0);
                // Release lock and return no stock error
                redisTemplate.execute(redisScript, Arrays.asList(lockKey), uuid);
                return RespBean.error(RespBeanEnum.NO_STOCK);
            }

            // 6. Send flash sale message to RabbitMQ for asynchronous processing
            // Create FlashSaleMessage object
            FlashSaleMessage flashSaleMessage = new FlashSaleMessage(user, goodsId);
            // Convert to JSON string
            String message = JSONUtil.toJsonStr(flashSaleMessage);
            // Send to RabbitMQ
            mqSender.sendFlashSaleMessage(message);

            // Return immediately with "IN_QUEUE" status for frontend polling
            return RespBean.error(RespBeanEnum.IN_QUEUE);
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Flash sale error: " + e.getMessage());
            e.printStackTrace();

            // Rollback Redis stock if needed
            String redisStockKey = "flashSaleStock:" + goodsId;
            redisTemplate.opsForValue().increment(redisStockKey);

            return RespBean.error(RespBeanEnum.ERROR);
        } finally {
            // Always release the lock using Lua script (atomic check-and-delete)
            redisTemplate.execute(redisScript, Arrays.asList(lockKey), uuid);
        }
    }

    /**
     * Get flash sale result (for frontend polling)
     * GET /api/v1/flash-sale/result/{goodsId}
     * 
     * Frontend should poll this endpoint to check if the order was created successfully
     * Returns:
     * - Order ID if successful
     * - Error if failed
     * - IN_QUEUE if still processing
     */
    @GetMapping("/result/{goodsId}")
    public RespBean getFlashSaleResult(@PathVariable Long goodsId,
                                     User user,
                                     HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Check if order was created successfully (stored in Redis by MQReceiver)
        FlashSaleOrder flashSaleOrder = (FlashSaleOrder) redisTemplate.opsForValue()
                .get("flashSaleOrder:" + user.getId() + ":" + goodsId);
        
        if (flashSaleOrder != null) {
            // Order created successfully, return order ID
            return RespBean.success(flashSaleOrder.getOrderId());
        }

        // Check if flash sale failed (marked in Redis by MQReceiver)
        String failKey = "flashSaleFail:" + user.getId() + ":" + goodsId;
        if (redisTemplate.hasKey(failKey)) {
            // Flash sale failed
            return RespBean.error(RespBeanEnum.NO_STOCK);
        }

        // Still processing (in queue)
        return RespBean.error(RespBeanEnum.IN_QUEUE);
    }

    /**
     * Initialize flash sale stock in Redis and local JVM memory
     * This method is executed after all properties of the class are initialized
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // Query all flash sale goods
        List<GoodsVo> goodsVoList = goodsService.getGoodsVo();
        // If the flash sale goods list is empty, return
        if (goodsVoList == null || goodsVoList.isEmpty()) {
            return;
        }
        for (GoodsVo goodsVo : goodsVoList) {
            // 1. Set flash sale stock to Redis (key: flashSaleStock:{goodsId}, value: flashSaleStock)
            redisTemplate.opsForValue().set("flashSaleStock:" + goodsVo.getId(), goodsVo.getFlashSaleStock());

            // 2. Set flash sale stock to local JVM memory
            // Initialize the goods stock map
            // If goodsId : false -> empty stock
            // If goodsId : true -> has stock
            goodsStockMap.put(goodsVo.getId(), goodsVo.getFlashSaleStock() > 0);
        }
    }
}
