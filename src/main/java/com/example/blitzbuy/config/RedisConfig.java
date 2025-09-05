package com.example.blitzbuy.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Heather
 * @version 1.0
 */
@Configuration
public class RedisConfig {
//    Custom RedisTemplate object, inject into container
//    Use this object to operate Redis later
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){

        RedisTemplate<String,Object>redisTemplate = new RedisTemplate<>();
//      Set key serialization
        redisTemplate.setKeySerializer(new StringRedisSerializer());
//      Value serialization:
//      Redis default is IDK serialization (binary), here using generic JSON data, no need to pass specific serialization objects
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//      Set hash serialization
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//      Inject connection factory
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public DefaultRedisScript<Long> redisScript(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("scripts/releaseLock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}