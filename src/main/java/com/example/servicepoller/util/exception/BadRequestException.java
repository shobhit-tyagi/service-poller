package com.example.servicepoller.util.exception;

import lombok.Getter;

import java.util.List;

public class BadRequestException extends RuntimeException implements BaseException, ResponseAware {

    private String message;
    private Object responseObject;

    public BadRequestException(final String message) {
        super(message);
        this.message = message;
    }

    public BadRequestException(final String message, final Object responseObject) {
        super(message);
        this.message = message;
        this.responseObject = responseObject;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getCode() {
        return 400;
    }

    @Override
    public String getStatus() {
        return "BAD_REQUEST";
    }

    @Override
    public Object getResponseObject() {
        return this.responseObject;
    }
}