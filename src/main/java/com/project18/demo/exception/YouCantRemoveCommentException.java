package com.project18.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class YouCantRemoveCommentException extends BaseException {
    public YouCantRemoveCommentException(String error, String code) {
        super(error, code);
    }
}
