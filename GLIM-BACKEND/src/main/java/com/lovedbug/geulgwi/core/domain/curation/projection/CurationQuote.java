package com.lovedbug.geulgwi.core.domain.curation.projection;

public interface CurationQuote {

    Long getCurationItemId();

    String getTitle();

    String getDescription();

    Long getQuoteId();

    Long getBookId();

    String getBookTitle();

    String getAuthor();

    String getPublisher();

    String getQuoteImageName();
}
