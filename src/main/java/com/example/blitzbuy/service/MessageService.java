package com.example.blitzbuy.service;

/**
 * 消息服务接口
 * 用于处理RabbitMQ消息的发送
 */
public interface MessageService {

    /**
     * 发送秒杀消息
     * @param message 消息内容
     */
    void sendFlashSaleMessage(String message);

    /**
     * 发送秒杀消息（带延迟）
     * @param message 消息内容
     * @param delayMillis 延迟时间（毫秒）
     */
    void sendFlashSaleMessageWithDelay(String message, long delayMillis);
}
