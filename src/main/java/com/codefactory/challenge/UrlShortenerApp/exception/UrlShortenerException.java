package com.codefactory.challenge.UrlShortenerApp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UrlShortenerException extends Exception {
    private HttpStatus httpStatus;

    public UrlShortenerException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
