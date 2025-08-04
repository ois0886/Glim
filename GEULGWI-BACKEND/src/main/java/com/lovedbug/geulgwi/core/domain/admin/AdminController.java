package com.lovedbug.geulgwi.core.domain.admin;


import com.lovedbug.geulgwi.core.domain.admin.dto.request.CreateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.request.UpdateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.response.CreateCurationResponse;
import com.lovedbug.geulgwi.core.domain.auth.dto.request.EmailVerificationRequest;
import com.lovedbug.geulgwi.core.domain.auth.dto.response.EmailVerificationResponse;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import com.lovedbug.geulgwi.core.domain.curation.entity.MainCuration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 *   추후에 Preauthorize 해주기
 * */

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/curations/main")
    public ResponseEntity<List<CurationItemResponse>> getMainCuration() {

        return ResponseEntity.ok(adminService.getMainCurationByAdmin());
    }

    @PostMapping("/curations")
    public ResponseEntity<CreateCurationResponse> createCuration(
            @RequestBody CreateCurationRequest createCurationRequest) {
        CreateCurationResponse createCurationResponse = adminService.createCuration(createCurationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createCurationResponse);
    }

    @PutMapping("/curations/items/{itemId}")
    public ResponseEntity<Void> updateCurationItem(
            @PathVariable Long itemId,
            @RequestBody UpdateCurationRequest updateCurationRequest
    ) {
        adminService.updateCurationItem(itemId, updateCurationRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/curations/items/{itemId}")
    public ResponseEntity<Void> deleteCurationItem(
            @PathVariable Long itemId
    ) {
        adminService.deleteCurationItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
