package com.chaeshin.boo.exception;

import lombok.Data;

@Data
public class ExceptionDto {

    private String msg;
    private int code;

    public ExceptionDto(CustomException e) {
        this.msg = e.getExceptions().getMsg();
        this.code = e.getExceptions().getCode();
    }
}
