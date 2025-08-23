package com.example.blitzbuy.controller;

import com.example.blitzbuy.service.MessageService;
import com.example.blitzbuy.vo.RespBean;
import com.example.blitzbuy.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * RabbitMQ Controller
 * Used to test RabbitMQ message sending functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/message")
public class RabbitMQController {

    @Autowired
    private MessageService messageService;

    /**
     * Send flash sale message
     * @param message message content
     * @return response result
     */
    @PostMapping("/send")
    public RespBean sendMessage(@RequestParam String message) {
        try {
            messageService.sendFlashSaleMessage(message);
            return RespBean.success("Message sending successfully");
        } catch (Exception e) {
            log.error("Send message failed", e);
            return RespBean.error(RespBeanEnum.ERROR, "Message sending failed: " + e.getMessage());
        }
    }

    /**
     * Send delayed flash sale message
     * @param message message content
     * @param delay delay time (milliseconds)
     * @return response result
     */
    @PostMapping("/send-delay")
    public RespBean sendDelayMessage(@RequestParam String message, @RequestParam(defaultValue = "5000") long delay) {
        try {
            messageService.sendFlashSaleMessageWithDelay(message, delay);
            return RespBean.success("Delayed message sending successfully, delay time: " + delay + "ms");
        } catch (Exception e) {
            log.error("Send delayed message failed", e);
            return RespBean.error(RespBeanEnum.ERROR, "Delayed message sending failed: " + e.getMessage());
        }
    }

    /**
     * Test RabbitMQ connection
     * @return response result
     */
    @GetMapping("/test")
    public RespBean testConnection() {
        try {
            // Send test message
            messageService.sendFlashSaleMessage("Test message - " + System.currentTimeMillis());
            return RespBean.success("RabbitMQ connection is normal");
        } catch (Exception e) {
            log.error("RabbitMQ connection test failed", e);
            return RespBean.error(RespBeanEnum.ERROR, "RabbitMQ connection failed: " + e.getMessage());
        }
    }
}
