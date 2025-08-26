package com.lovedbug.geulgwi.core.common.exception;

import com.lovedbug.geulgwi.core.common.exception.constant.CommonErrorCode;
import com.lovedbug.geulgwi.core.domain.auth.exception.AuthException;
import com.lovedbug.geulgwi.core.domain.member.exception.MemberException;
import com.lovedbug.geulgwi.external.email.exception.EmailException;
import com.lovedbug.geulgwi.external.image.exception.ImageException;
import lombok.extern.slf4j.Slf4j;
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

        return buildErrorResponse(e.getErrorCode(), e.getDetail());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<GeulgwiErrorResponse> handleAuthException(AuthException e) {

        log.warn("AuthException: code={}, detail={}", e.getErrorCode().name(), e.getDetail());

        return buildErrorResponse(e.getErrorCode(), e.getDetail());
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<GeulgwiErrorResponse> handleEmailException(EmailException e) {

        return buildErrorResponse(e.getErrorCode(), e.getDetail());
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestPartException.class
    })
    public ResponseEntity<GeulgwiErrorResponse> handleBadRequest(Exception e) {

        log.warn("Bad request: {}", e.getMessage());

        return buildErrorResponse(CommonErrorCode.INVALID_PARAMETER, e.getMessage());
    }

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<GeulgwiErrorResponse> handleImageException(ImageException e) {
        log.warn("ImageException: code={}, detail={}", e.getErrorCode().name(), e.getDetail());
        return buildErrorResponse(e.getErrorCode(), e.getDetail());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeulgwiErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);

        return buildErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR, null);
    }

    private ResponseEntity<GeulgwiErrorResponse> buildErrorResponse(ErrorCode errorCode, String detail) {

        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(GeulgwiErrorResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .detail(detail)
                .build());
    }
}
