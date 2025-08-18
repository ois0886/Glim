package com.lovedbug.geulgwi.core.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisKey {

    POPULAR_QUOTES("popular:quotes"),
    POPULAR_BOOKS("popular:books");

    private final String key;
}
