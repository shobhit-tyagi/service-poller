package com.example.servicepoller.util.exception;

public class NotFoundException extends RuntimeException implements BaseException {

    private String message;

    public NotFoundException(final String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getCode() {
        return 404;
    }

    @Override
    public String getStatus() {
        return "NOT_FOUND";
    }
}