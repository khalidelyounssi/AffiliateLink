package com.affiliateLink.profile.controller;

import com.affiliateLink.profile.dto.ChangeStatusRequest;
import com.affiliateLink.profile.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/profiles")
public class AdminProfileController {
    private final ProfileService service;

    public AdminProfileController(ProfileService service) {
        this.service = service;
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request) {
        service.changeStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }
}