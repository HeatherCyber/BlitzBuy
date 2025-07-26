package com.example.blitzbuy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Heather
 * @version 1.0
 */
@Configuration
public class RedisConfig {
//    自定义RedisTemplate对象，注入到容器
//    后续通过该对象操作Redis
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){

        RedisTemplate<String,Object>redisTemplate = new RedisTemplate<>();
//      设置相应key的序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
//      value序列化：
//      redis默认是idk的序列化(二进制),这里使用的是通用的json数据,不用传具体的序列化的对象
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//      设置相应hash序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//      注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}