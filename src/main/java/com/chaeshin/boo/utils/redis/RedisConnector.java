package com.chaeshin.boo.utils.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisConnector {

    private final RedisTemplate<String, String> redisTemplate;

    public void set(final String key, final String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setWithTtl(String key, String value, long exp) {
        redisTemplate.opsForValue().set(key, value, exp, TimeUnit.MILLISECONDS);
    }

    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean exists(final String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void remove(final String key) {
        redisTemplate.delete(key);
    }
}
