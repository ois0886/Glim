package com.lovedbug.geulgwi.core.domain.curation.dto.response;

import lombok.Builder;
import java.util.List;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;

@Builder
public record CurationItemResponse(
    Long curationItemId,
    String title,
    String description,
    CurationType curationType,
    List<CurationContentResponse> contents
) {
}
