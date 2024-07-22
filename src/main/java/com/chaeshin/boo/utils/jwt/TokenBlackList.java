package com.chaeshin.boo.utils.jwt;

import java.util.Date;

public interface TokenBlackList {

    void put(String token, Date date);

    boolean containsKey(String token);
}
