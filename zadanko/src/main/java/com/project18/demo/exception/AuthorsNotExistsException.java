package com.project18.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AuthorsNotExistsException extends BaseException {
    public AuthorsNotExistsException(String autor) {
        super("Autor: " + autor + " nieistnieje!", "code");
    }
}
