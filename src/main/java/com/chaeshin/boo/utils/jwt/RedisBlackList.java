package com.chaeshin.boo.utils.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component("redisBlackList")
@RequiredArgsConstructor
@Primary
public class RedisBlackList implements TokenBlackList{

    private final RedisTemplate<String, Date> redisBlackList;

    @Override
    public void put(String token, Date date) {
        Long ttl = date.getTime() - new Date(System.currentTimeMillis()).getTime();
        redisBlackList.opsForValue().set(token, date, ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean containsKey(String token) {
        if (redisBlackList.opsForValue().get(token) == null) {
            return false;
        }
        return true;
    }
}
