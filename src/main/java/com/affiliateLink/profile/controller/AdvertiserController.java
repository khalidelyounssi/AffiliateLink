package com.affiliateLink.profile.controller;

import com.affiliateLink.profile.dto.*;
import com.affiliateLink.profile.service.AdvertiserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/advertisers")
public class AdvertiserController {
    private final AdvertiserService service;

    public AdvertiserController(AdvertiserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AdvertiserResponse> create(
            @Valid @RequestBody CreateAdvertiserRequest request) {
        AdvertiserResponse response = service.create(request);
        return ResponseEntity.created(
                URI.create("/api/advertisers/" + response.id())
        ).body(response);
    }
    @GetMapping("/{id}")
    public AdvertiserResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<AdvertiserResponse> findAll() {
        return service.findAll();
    }


}