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
    public static final String FLASH_SALE_ROUTING_KEY = "flash.sale.routing.key";

    // Dead letter queue related
    public static final String FLASH_SALE_DLX_QUEUE = "flash.sale.dlx.queue";
    public static final String FLASH_SALE_DLX_EXCHANGE = "flash.sale.dlx.exchange";
    public static final String FLASH_SALE_DLX_ROUTING_KEY = "flash.sale.dlx.routing.key";

    /**
     * Flash sale queue
     */
    @Bean
    public Queue flashSaleQueue() {
        return new Queue(FLASH_SALE_QUEUE, true);
        
        // return QueueBuilder.durable(FLASH_SALE_QUEUE)
        //         .withArgument("x-dead-letter-exchange", FLASH_SALE_DLX_EXCHANGE)
        //         .withArgument("x-dead-letter-routing-key", FLASH_SALE_DLX_ROUTING_KEY)
        //         .withArgument("x-message-ttl", 30000) // 30 seconds expiration
        //         .build();
    }

    /**
     * Flash sale exchange
     */
    @Bean
    public DirectExchange flashSaleExchange() {
        return new DirectExchange(FLASH_SALE_EXCHANGE);
    }

    /**
     * Bind queue and exchange
     */
    @Bean
    public Binding flashSaleBinding() {
        return BindingBuilder.bind(flashSaleQueue())
                .to(flashSaleExchange())
                .with(FLASH_SALE_ROUTING_KEY);
    }

    /**
     * Dead letter queue
     */
    @Bean
    public Queue flashSaleDlxQueue() {
        return QueueBuilder.durable(FLASH_SALE_DLX_QUEUE).build();
    }

    /**
     * Dead letter exchange
     */
    @Bean
    public DirectExchange flashSaleDlxExchange() {
        return new DirectExchange(FLASH_SALE_DLX_EXCHANGE);
    }

    /**
     * Bind dead letter queue and dead letter exchange
     */
    @Bean
    public Binding flashSaleDlxBinding() {
        return BindingBuilder.bind(flashSaleDlxQueue())
                .to(flashSaleDlxExchange())
                .with(FLASH_SALE_DLX_ROUTING_KEY);
    }
}
