package com.affiliateLink.offer.controller;

import com.affiliateLink.offer.dto.*;
import com.affiliateLink.offer.entity.CampaignStatus;
import com.affiliateLink.offer.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
public class CampaignController {
    private final CampaignService service;

    public CampaignController(CampaignService service) {
        this.service = service;
    }

    @PostMapping("/api/offres/{id}/campaigns")
    public ResponseEntity<CampaignResponse> create(
            @PathVariable Long id, @Valid @RequestBody CreateCampaignRequest request) {
        CampaignResponse response = service.create(id, request);
        return ResponseEntity.created(URI.create("/api/offres/" + id)).body(response);
    }

    @PatchMapping("/api/campaigns/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id, @Valid @RequestBody ChangeCampaignStatusRequest request) {
        service.changeStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }
}
