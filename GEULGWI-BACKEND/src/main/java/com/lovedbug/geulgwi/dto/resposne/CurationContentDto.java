package com.lovedbug.geulgwi.dto.resposne;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CurationContentDto {

    private Long bookId;
    private String bookTitle;
    private String author;
    private String publisher;

    private String bookCoverUrl;

    private Long quoteId;
    private String imageName;
}
