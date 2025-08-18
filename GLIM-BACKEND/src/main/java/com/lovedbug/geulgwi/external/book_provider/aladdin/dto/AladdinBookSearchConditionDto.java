package com.lovedbug.geulgwi.external.book_provider.aladdin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AladdinBookSearchConditionDto extends AladdinBookCommonCondition {

    private final String queryType;
    private final String query;
    private final int start;

    @Builder
    public AladdinBookSearchConditionDto(String query, String queryType, int start) {
        super();
        this.query = query;
        this.queryType = queryType;
        this.start = start;
    }
}
