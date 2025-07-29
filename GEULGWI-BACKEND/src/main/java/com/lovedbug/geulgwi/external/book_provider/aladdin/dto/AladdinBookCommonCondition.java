package com.lovedbug.geulgwi.external.book_provider.aladdin.dto;

import lombok.Getter;

@Getter
public abstract class AladdinBookCommonCondition {

    protected final int maxResults = 10;
    protected final String searchTarget = "Book";
    protected final String output = "JS";
    protected final String version = "20131101";
}
