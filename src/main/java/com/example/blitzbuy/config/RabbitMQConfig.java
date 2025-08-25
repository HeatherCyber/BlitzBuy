package com.example.blitzbuy.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration class
 * Used to configure queues, exchanges, and binding relationships
 */
@Configuration
public class RabbitMQConfig {

    // Queue name constants
    public static final String FLASH_SALE_QUEUE = "flash.sale.queue";
    public static final String FLASH_SALE_EXCHANGE = "flash.sale.exchange";
    // The routing key is "flash.sale.#", which means all messages with the routing key "flash.sale.#" will be sent to the flash sale queue
    public static final String FLASH_SALE_ROUTING_KEY = "flash.sale.#";


    /**
     * Create flash sale queue
     */
    @Bean
    public Queue flashSaleQueue() {
        return new Queue(FLASH_SALE_QUEUE);   
    }

    /**
     * Create flash sale exchange
     */
    @Bean
    public TopicExchange flashSaleExchange() {
        return new TopicExchange(FLASH_SALE_EXCHANGE);
    }

    /**
     * Bind flash sale queue and exchange
     */
    @Bean
    public Binding flashSaleBinding() {
        return BindingBuilder.bind(flashSaleQueue()).to(flashSaleExchange()).with(FLASH_SALE_ROUTING_KEY);
    }
    

}
