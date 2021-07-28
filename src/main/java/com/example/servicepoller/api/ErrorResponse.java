package com.example.servicepoller.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
    String id;
    String status;
    int code;
    String message;
    String details;
}
