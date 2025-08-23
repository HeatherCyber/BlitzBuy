package com.example.blitzbuy.service.impl;

import com.example.blitzbuy.config.RabbitMQConfig;
import com.example.blitzbuy.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息服务实现类
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
            log.info("秒杀消息发送成功: {}", message);
        } catch (Exception e) {
            log.error("秒杀消息发送失败: {}", message, e);
            throw new RuntimeException("消息发送失败", e);
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
            log.info("延迟秒杀消息发送成功: {}, 延迟: {}ms", message, delayMillis);
        } catch (Exception e) {
            log.error("延迟秒杀消息发送失败: {}", message, e);
            throw new RuntimeException("延迟消息发送失败", e);
        }
    }
}
