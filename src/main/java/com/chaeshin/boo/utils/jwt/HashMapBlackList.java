package com.chaeshin.boo.utils.jwt;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component("hashMapBlackList")
public class HashMapBlackList implements TokenBlackList{

    private ConcurrentHashMap<String, Date> blackList;
    private ScheduledExecutorService scheduler;


    @Override
    public void put(String token, Date date) {
        blackList.put(token, date);
    }


    @Override
    public boolean containsKey(String token) {
        return blackList.containsKey(token);
    }


    /*어플리케이션이 실행될 때마다 블랙리스트 및 삭제 스케쥴러 재시작*/
    @PostConstruct
    private void init() {
        blackList = new ConcurrentHashMap<>();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::removeExpiredTokens, 0, 1, TimeUnit.HOURS);
    }

    /*블랙리스트 내 토큰 자동 삭제*/
    private void removeExpiredTokens() {
        Date now = new Date();
        blackList.entrySet().removeIf(entry -> entry.getValue().before(now));
    }

    /*어플리케이션이 종료되면 블랙리스트 자동 삭제 스케쥴러 종료*/
    @PreDestroy
    private void cleanup() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
