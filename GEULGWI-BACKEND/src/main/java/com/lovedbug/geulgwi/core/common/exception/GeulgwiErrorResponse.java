package com.lovedbug.geulgwi.core.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class GeulgwiErrorResponse {

    private int status;
    private String message;
}
