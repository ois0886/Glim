package com.lovedbug.geulgwi.dto.resposne;

import lombok.Builder;
import java.util.List;
import com.lovedbug.geulgwi.constant.CurationType;

@Builder
public record CurationItemDto(
    Long curationItemId,
    String title,
    String description,
    CurationType curationType,
    List<CurationContentDto> contents
) {
}
