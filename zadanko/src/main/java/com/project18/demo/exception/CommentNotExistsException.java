package com.project18.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CommentNotExistsException extends BaseException {
    public CommentNotExistsException(long id) {
        super("Komentarz o id: " + id + " nieistnieje!", "code");
    }
}
