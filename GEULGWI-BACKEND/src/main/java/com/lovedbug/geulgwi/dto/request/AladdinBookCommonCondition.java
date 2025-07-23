package com.lovedbug.geulgwi.dto.request;

import lombok.Getter;

@Getter
public abstract class AladdinBookCommonCondition {

    protected final int maxResults = 2;
    protected final String searchTarget = "Book";
    protected final String output = "JS";
    protected final String version = "20131101";
}
