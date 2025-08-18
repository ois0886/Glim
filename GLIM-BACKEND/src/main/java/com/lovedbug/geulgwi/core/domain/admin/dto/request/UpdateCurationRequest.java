package com.lovedbug.geulgwi.core.domain.admin.dto.request;

import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Builder
@Getter
public class UpdateCurationRequest {

    private final String name;

    private final String description;

    private final CurationType curationType;

    private final List<Long> bookIds;

    private final List<Long> quoteIds;
}
