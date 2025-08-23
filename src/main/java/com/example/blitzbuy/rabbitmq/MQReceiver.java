package com.example.blitzbuy.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.example.blitzbuy.config.RabbitMQConfig;

/**
 * MQReceiver
 * Used to receive messages from RabbitMQ
 */
@Service
@Slf4j
public class MQReceiver {

    @RabbitListener(queues = RabbitMQConfig.FLASH_SALE_QUEUE)
    public void receiveFlashSaleMessage(String message) {
        System.out.println("Received message: " + message);
    }
}