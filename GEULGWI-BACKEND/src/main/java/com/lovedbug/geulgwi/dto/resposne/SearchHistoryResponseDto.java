package com.lovedbug.geulgwi.dto.resposne;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Builder
@Getter
public class SearchHistoryResponseDto {

    private String author1;
    private String author2;
    private String author3;
    private String author4;
    private String author5;
    private String author6;
    private String author7;
    private String author8;
    private String author9;
    private String author10;

    public static List<SearchHistoryResponseDto> toSearchHistoryDto(String keyword){

        SearchHistoryResponseDto historyResponse = SearchHistoryResponseDto.builder()
            .author1(keyword + " 이문열")
            .author2(keyword + " 조정래")
            .author3(keyword + " 한강")
            .author4(keyword + " 김영하")
            .author5(keyword + " 정세랑")
            .author6(keyword + " 베르나르 베르베르")
            .author7(keyword + " 히가시노 게이고")
            .author8(keyword + " 무라카미 하루키")
            .author9(keyword + " 조앤 K. 롤링")
            .author10(keyword + " 유발 하라리")
            .build();

        return List.of(historyResponse);
    }
}
