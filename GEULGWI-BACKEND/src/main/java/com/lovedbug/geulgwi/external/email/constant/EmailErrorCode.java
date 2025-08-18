package com.lovedbug.geulgwi.external.email.constant;

import com.lovedbug.geulgwi.core.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EmailErrorCode implements ErrorCode {

   EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "EMAIL-001", "이미 가입된 이메일 입니다."),
   EMAIL_SEND_FAILED(HttpStatus.UNAUTHORIZED, "EMAIL-002", "이메일 발송에 실패 햤습니다."),
   EMAIL_TEMPLATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-003", "이메일 템플릿 렌더링 처리에 실패 했습니다.");

   private final HttpStatus httpStatus;
   private final String code;
   private final String message;
}
