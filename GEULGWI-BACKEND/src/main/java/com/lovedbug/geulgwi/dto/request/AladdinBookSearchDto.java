package com.lovedbug.geulgwi.dto.request;

import com.lovedbug.geulgwi.dto.resposne.AladdinBookDto;
import lombok.Getter;
import lombok.ToString;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@ToString
@Getter
public class AladdinBookSearchDto {

    private String version;
    private String logo;
    private String title;
    private String link;

    @JsonProperty("pubDate")
    private String publishedDate;

    private int totalResults;
    private int startIndex;
    private int itemsPerPage;
    private String query;

    private int searchCategoryId;
    private String searchCategoryName;

    @JsonProperty("item")
    private List<AladdinBookDto> items;
}
