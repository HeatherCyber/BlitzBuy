package com.example.blitzbuy.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.blitzbuy.config.RabbitMQConfig;

import jakarta.annotation.Resource;

/**
 * MQSender
 * Used to send messages to RabbitMQ
 */
@Service
public class MQSender {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendFlashSaleMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.FLASH_SALE_EXCHANGE, message);
    }
}
