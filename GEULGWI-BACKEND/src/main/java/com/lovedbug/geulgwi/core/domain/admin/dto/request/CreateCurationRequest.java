package com.lovedbug.geulgwi.core.domain.admin.dto.request;


import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateCurationRequest {

    private String name;
    private String description;
    private CurationType curationType;
    private List<Long> ids;
}
