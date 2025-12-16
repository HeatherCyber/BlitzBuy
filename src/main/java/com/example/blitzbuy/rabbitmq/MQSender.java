package com.example.blitzbuy.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.blitzbuy.config.RabbitMQConfig;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * MQSender
 * Used to send flash sale messages to RabbitMQ
 */
@Service
@Slf4j
public class MQSender {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * Send flash sale message to RabbitMQ
     * @param message
     */
    public void sendFlashSaleMessage(String message) {
        log.info("Sending message: {}", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.FLASH_SALE_EXCHANGE, RabbitMQConfig.FLASH_SALE_ROUTING_KEY, message);
    }
}
