package com.lovedbug.geulgwi.core.domain.curation.dto;

import lombok.Builder;
import java.util.List;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;

@Builder
public record CurationItemDto(
    Long curationItemId,
    String title,
    String description,
    CurationType curationType,
    List<CurationContentDto> contents
) {
}
