package com.chaeshin.boo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Date;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setPassword(password);
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Date> redisTemplate() {
        RedisTemplate<String, Date> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());

//        PolymorphicTypeValidator validator = BasicPolymorphicTypeValidator
//                .builder().allowIfSubType(Date.class)
//                        .build();
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Date.class));
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
