package com.lovedbug.geulgwi.external.image.exception;

import com.lovedbug.geulgwi.core.common.exception.GeulgwiException;
import com.lovedbug.geulgwi.external.image.constant.ImageErrorCode;
import lombok.Getter;

@Getter
public class ImageException extends GeulgwiException {

    private final ImageErrorCode errorCode;
    private final String detail;

    public ImageException(ImageErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = null;
    }

    public ImageException(ImageErrorCode errorCode, String detail) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
