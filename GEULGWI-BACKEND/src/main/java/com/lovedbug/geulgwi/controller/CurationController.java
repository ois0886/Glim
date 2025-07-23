package com.lovedbug.geulgwi.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lovedbug.geulgwi.dto.resposne.CurationItemDto;
import com.lovedbug.geulgwi.service.CurationService;

@RestController
@RequestMapping("/api/v1/curations")
@RequiredArgsConstructor
public class CurationController {

    private final CurationService curationService;

    @GetMapping("/main")
    public ResponseEntity<List<CurationItemDto>> getMainCuration() {

        return ResponseEntity.ok(curationService.getMainCuration());
    }
}
