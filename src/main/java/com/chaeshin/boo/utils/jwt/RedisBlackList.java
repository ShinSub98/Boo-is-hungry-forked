package com.chaeshin.boo.utils.jwt;

import com.chaeshin.boo.utils.redis.RedisConnector;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Component("redisBlackList")
@RequiredArgsConstructor
@Primary
public class RedisBlackList implements TokenBlackList{

    private final RedisConnector redisConnector;
    private SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

    @Override
    public void put(String token, String date) {
        Date parsedDate;
        try {
            parsedDate = formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Long ttl = parsedDate.getTime() - new Date(System.currentTimeMillis()).getTime();
        redisConnector.setWithTtl(token, date, ttl);
    }

    @Override
    public boolean containsKey(String token) {
        return redisConnector.exists(token);
    }
}
