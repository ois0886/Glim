package com.lovedbug.geulgwi.core.domain.search;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Builder
@Getter
public class SearchHistoryResponseDto {

    private List<String> searchHistory;

    public static SearchHistoryResponseDto toSearchDto(){

       return SearchHistoryResponseDto.builder()
           .searchHistory(List.of(
               "이문열",
               "조정래",
               "한강",
               "김영하",
               "정세랑",
               "베르나르 베르베르",
               "히가시노 게이고",
               "무라카미 하루키",
               "조앤 K. 롤링",
               "유발 하라리"
           )).build();
    }
}
