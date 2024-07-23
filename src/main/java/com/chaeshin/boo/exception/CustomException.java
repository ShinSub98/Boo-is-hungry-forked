package com.chaeshin.boo.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomException extends RuntimeException {

    private final Exceptions exceptions;

    public Exceptions getExceptions() {
        return exceptions;
    }

    public int getCode() {
        return exceptions.getCode();
    }

    public String getMsg() {
        return exceptions.getMsg();
    }
}
