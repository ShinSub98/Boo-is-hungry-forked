package com.chaeshin.boo.utils.jwt;

public interface TokenBlackList {

    void put(String token, String date);

    boolean containsKey(String token);
}
