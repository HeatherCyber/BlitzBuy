package com.example.blitzbuy.service.impl;

import com.example.blitzbuy.config.RabbitMQConfig;
import com.example.blitzbuy.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Message Service Implementation Class
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendFlashSaleMessage(String message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.FLASH_SALE_EXCHANGE,
                    RabbitMQConfig.FLASH_SALE_ROUTING_KEY,
                    message
            );
            log.info("Flash sale message sent successfully: {}", message);
        } catch (Exception e) {
            log.error("Flash sale message sending failed: {}", message, e);
            throw new RuntimeException("Message sending failed", e);
        }
    }

    @Override
    public void sendFlashSaleMessageWithDelay(String message, long delayMillis) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.FLASH_SALE_EXCHANGE,
                    RabbitMQConfig.FLASH_SALE_ROUTING_KEY,
                    message,
                    msg -> {
                        msg.getMessageProperties().setHeader("x-delay", delayMillis);
                        return msg;
                    }
            );
            log.info("Delayed flash sale message sent successfully: {}, delay: {}ms", message, delayMillis);
        } catch (Exception e) {
            log.error("Delayed flash sale message sending failed: {}", message, e);
            throw new RuntimeException("Delayed message sending failed", e);
        }
    }
}
