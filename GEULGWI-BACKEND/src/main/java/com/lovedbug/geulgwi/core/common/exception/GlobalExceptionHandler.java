package com.lovedbug.geulgwi.core.common.exception;

import com.lovedbug.geulgwi.core.common.exception.constant.CommonErrorCode;
import com.lovedbug.geulgwi.core.domain.member.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<GeulgwiErrorResponse> handleMemberException(MemberException e) {

        log.warn("MemberException: code={}, detail={}", e.getErrorCode().name(), e.getDetail());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(GeulgwiErrorResponse.builder()
                .status(e.getErrorCode().getHttpStatus().value())
                .code(e.getErrorCode().name())
                .message(e.getErrorCode().getMessage())
                .detail(e.getDetail())
                .build());
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestPartException.class
    })
    public ResponseEntity<GeulgwiErrorResponse> handleBadRequest(Exception e) {
        log.warn("Bad request: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(GeulgwiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(CommonErrorCode.INVALID_PARAMETER.name())
                .message(CommonErrorCode.INVALID_PARAMETER.getMessage())
                .detail(e.getMessage())
                .build());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeulgwiErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(GeulgwiErrorResponse.builder()
                .status(CommonErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value())
                .code(CommonErrorCode.INTERNAL_SERVER_ERROR.name())
                .message(CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .build());
    }
}
