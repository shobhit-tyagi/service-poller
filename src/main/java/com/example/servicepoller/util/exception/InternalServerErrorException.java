package com.example.servicepoller.util.exception;

public class InternalServerErrorException extends RuntimeException implements BaseException {

    private String message;

    public InternalServerErrorException(final String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getCode() {
        return 500;
    }

    @Override
    public String getStatus() {
        return "INTERNAL_SERVER_ERROR";
    }
}
