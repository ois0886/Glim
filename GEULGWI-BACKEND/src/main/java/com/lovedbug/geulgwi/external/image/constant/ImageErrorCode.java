package com.lovedbug.geulgwi.external.image.constant;

import com.lovedbug.geulgwi.core.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {

    INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 이미지 형식입니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    IMAGE_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 읽기에 실패했습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패했습니다."),
    STORAGE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "스토리지 서비스 이용이 불가능합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
