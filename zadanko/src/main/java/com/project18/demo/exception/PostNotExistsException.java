package com.project18.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PostNotExistsException extends BaseException {
    public PostNotExistsException(long id) {
        super("Post o id: " + id + " nieistnieje!", "code");
    }
}
