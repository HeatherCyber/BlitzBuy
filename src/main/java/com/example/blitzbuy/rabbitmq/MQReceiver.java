package com.example.blitzbuy.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


import com.example.blitzbuy.pojo.FlashSaleMessage;
import com.example.blitzbuy.pojo.User;
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


    @RabbitListener(queues = RabbitMQConfig.FLASH_SALE_QUEUE)
    public void receiveFlashSaleMessage(String message) {
        // Log the received message
        log.info("Received message: {}", message);
        // Parse the message to FlashSaleMessage
        FlashSaleMessage flashSaleMessage = JSONUtil.toBean(message, FlashSaleMessage.class);
        // get the user
        User user = flashSaleMessage.getUser();
        // get the goods id
        Long goodsId = flashSaleMessage.getGoodsId();
        // Get the goods
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        
        // Create an order
        orderService.creatFlashSaleOrder(user, goodsVo);
    }
}