package com.example.blitzbuy.service;

/**
 * Message Service Interface
 * Used for handling RabbitMQ message sending
 */
public interface MessageService {

    /**
     * Send flash sale message
     * @param message message content
     */
    void sendFlashSaleMessage(String message);

    /**
     * Send flash sale message (with delay)
     * @param message message content
     * @param delayMillis delay time (milliseconds)
     */
    void sendFlashSaleMessageWithDelay(String message, long delayMillis);
}
