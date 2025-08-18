package com.lovedbug.geulgwi.core.domain.curation;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;

@RestController
@RequestMapping("/api/v1/curations")
@RequiredArgsConstructor
public class CurationController {

    private final CurationService curationService;

    @GetMapping("/main")
    public ResponseEntity<List<CurationItemResponse>> getMainCuration() {

        return ResponseEntity.ok(curationService.getMainCuration());
    }
}
