package com.project18.demo.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final String message;
    private final String code;

    public BaseException(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
