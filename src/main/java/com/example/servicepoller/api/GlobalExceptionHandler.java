package com.example.servicepoller.api;

import com.example.servicepoller.util.exception.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBadRequestError(final HttpMessageNotReadableException exception) {
        return toError(new BadRequestException(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBadRequestError(final BadRequestException exception) {
        return toError(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundError(final NotFoundException exception) {
        return toError(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInternalError(final InternalServerErrorException exception) {
        return toError(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> fallbackExceptionHandler(final RuntimeException exception) {
        log.error("Unknown error",exception);
        return toError(new InternalServerErrorException(exception.getMessage()));
    }

    @SneakyThrows
    private ResponseEntity<ErrorResponse> toError(final RuntimeException exception) {
        val base = (BaseException) exception;
        val errorCode = Optional.ofNullable(base.getCode()).orElse(500);
        return ResponseEntity.status(HttpStatus.resolve(errorCode))
                .body(ErrorResponse.builder()
                        .id(UUID.randomUUID().toString())
                        .status(base.getStatus())
                        .code(base.getCode())
                        .message(base.getMessage())
                        .details((exception instanceof ResponseAware)
                                ? objectMapper.writeValueAsString(((ResponseAware)exception).getResponseObject())
                                : null)
                        .build());
    }
}
