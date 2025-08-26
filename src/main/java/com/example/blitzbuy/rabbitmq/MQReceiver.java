package com.example.blitzbuy.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

import com.example.blitzbuy.pojo.FlashSaleMessage;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.pojo.Order;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import com.example.blitzbuy.config.RabbitMQConfig;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.vo.GoodsVo;

import cn.hutool.json.JSONUtil;

/**
 * MQReceiver
 * Used to receive messages from RabbitMQ
 */
@Service
@Slf4j
public class MQReceiver {
    // Inject the goods service
    @Resource
    private GoodsService goodsService;

    // Inject the order service
    @Resource
    private OrderService orderService;

    // Inject Redis template
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @RabbitListener(queues = RabbitMQConfig.FLASH_SALE_QUEUE)
    public void receiveFlashSaleMessage(String message) {
        try {
            // Log the received message
            log.info("Received message: {}", message);
            
            // Parse the message to FlashSaleMessage
            FlashSaleMessage flashSaleMessage = JSONUtil.toBean(message, FlashSaleMessage.class);
            if (flashSaleMessage == null) {
                log.error("Failed to parse message: {}", message);
                return;
            }
            
            // get the user
            User user = flashSaleMessage.getUser();
            if (user == null) {
                log.error("User is null in message: {}", message);
                return;
            }
            
            // get the goods id
            Long goodsId = flashSaleMessage.getGoodsId();
            if (goodsId == null) {
                log.error("GoodsId is null in message: {}", message);
                return;
            }
            
            // Get the goods
            GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
            if (goodsVo == null) {
                log.error("Goods not found for goodsId: {}", goodsId);
                // Mark as failed in Redis
                redisTemplate.opsForValue().set("flashSaleFail:" + user.getId() + ":" + goodsId, "0");
                return;
            }
            
            // Create an order
            Order order = orderService.creatFlashSaleOrder(user, goodsVo);
            if (order == null) {
                log.error("Failed to create flash sale order for user: {}, goodsId: {}", user.getId(), goodsId);
                // Mark as failed in Redis
                redisTemplate.opsForValue().set("flashSaleFail:" + user.getId() + ":" + goodsId, "0", 60, TimeUnit.SECONDS);
            } else {
                log.info("Successfully created flash sale order: {} for user: {}, goodsId: {}", order.getId(), user.getId(), goodsId);
            }
            
        } catch (Exception e) {
            log.error("Error processing flash sale message: {}", message, e);
            // Try to extract user and goodsId for failure marking
            try {
                FlashSaleMessage flashSaleMessage = JSONUtil.toBean(message, FlashSaleMessage.class);
                if (flashSaleMessage != null && flashSaleMessage.getUser() != null && flashSaleMessage.getGoodsId() != null) {
                    redisTemplate.opsForValue().set("flashSaleFail:" + flashSaleMessage.getUser().getId() + ":" + flashSaleMessage.getGoodsId(), "0");
                }
            } catch (Exception ex) {
                log.error("Failed to mark flash sale as failed", ex);
            }
        }
    }
}