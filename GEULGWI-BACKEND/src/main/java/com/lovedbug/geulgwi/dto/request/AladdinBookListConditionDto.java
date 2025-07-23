package com.lovedbug.geulgwi.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AladdinBookListConditionDto extends AladdinBookCommonCondition {

    private final String queryType;
    private final int start;

    @Builder
    public AladdinBookListConditionDto(String queryType, int start) {
        super();
        this.queryType = queryType;
        this.start = start;
    }
}
