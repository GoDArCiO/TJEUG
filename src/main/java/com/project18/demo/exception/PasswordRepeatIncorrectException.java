package com.project18.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PasswordRepeatIncorrectException extends BaseException {
    public PasswordRepeatIncorrectException(String error, String code) {
        super(error, code);
    }
}
