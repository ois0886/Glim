package com.lovedbug.geulgwi.core.domain.curation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CurationContentResponse {

    private Long bookId;
    private String bookTitle;
    private String author;
    private String publisher;

    private String bookCoverUrl;

    private Long quoteId;
    private String imageName;
}
