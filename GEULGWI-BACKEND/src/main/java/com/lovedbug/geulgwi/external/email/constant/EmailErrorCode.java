package com.lovedbug.geulgwi.external.email.constant;

import com.lovedbug.geulgwi.core.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailErrorCode implements ErrorCode {

   EMAIL_ALREADY_EXISTS(),
   EMAIL_SEND_FAILED(),
   EMAIL_TEMPLATE_ERROR
}
