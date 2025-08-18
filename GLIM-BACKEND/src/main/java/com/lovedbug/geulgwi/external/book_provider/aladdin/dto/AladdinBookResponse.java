package com.lovedbug.geulgwi.external.book_provider.aladdin.dto;

import lombok.Getter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonSetter;

@ToString
@Getter
public class AladdinBookResponse {

    private String title;
    private String linkUrl;
    private String author;
    private String translator;
    private String publishedDate;
    private String description;

    private String isbn;
    private String isbn13;

    private int priceSales;
    private int priceStandard;
    private String mallType;
    private String coverUrl;

    private int categoryId;
    private String categoryName;
    private String publisher;

    private boolean adult;

    @JsonSetter("cover")
    public void setCoverForRequest(String cover) {
        this.coverUrl = cover;
    }

    @JsonSetter("link")
    public void setLinkForRequest(String link) {
        this.linkUrl = link;
    }

    @JsonSetter("pubDate")
    public void setPubDateForRequest(String pubDate) {
        this.publishedDate = pubDate;
    }
}
