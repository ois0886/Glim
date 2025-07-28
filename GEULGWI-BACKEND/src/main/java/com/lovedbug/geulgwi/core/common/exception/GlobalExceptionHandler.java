package com.lovedbug.geulgwi.core.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeulgwiErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(GeulgwiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal Server Error")
                .build());
    }
}
