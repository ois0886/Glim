package com.lovedbug.geulgwi.core.domain.admin.curation;


import com.lovedbug.geulgwi.core.domain.admin.dto.request.CreateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.request.UpdateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.response.CreateCurationResponse;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 *   추후에 Preauthorize 해주기
 * */

@RestController
@RequestMapping("/api/v1/admin/curations")
@RequiredArgsConstructor
public class AdminCurationController {
    private final AdminCurationService adminCurationService;

    @GetMapping("/main")
    public ResponseEntity<List<CurationItemResponse>> getMainCuration() {

        return ResponseEntity.ok(adminCurationService.getMainCurationByAdmin());
    }

    @PostMapping("")
    public ResponseEntity<CreateCurationResponse> createCuration(
            @RequestBody CreateCurationRequest createCurationRequest) {
        CreateCurationResponse createCurationResponse = adminCurationService.createCuration(createCurationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createCurationResponse);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Void> updateCurationItem(
            @PathVariable Long itemId,
            @RequestBody UpdateCurationRequest updateCurationRequest
    ) {
        adminCurationService.updateCurationItem(itemId, updateCurationRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteCurationItem(
            @PathVariable Long itemId
    ) {
        adminCurationService.deleteCurationItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
