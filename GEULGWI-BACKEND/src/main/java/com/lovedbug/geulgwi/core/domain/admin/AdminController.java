package com.lovedbug.geulgwi.core.domain.admin;


import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
