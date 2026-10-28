package com.affiliateLink.offer.controller;

import com.affiliateLink.offer.dto.*;
import com.affiliateLink.offer.entity.OfferNiche;
import com.affiliateLink.offer.service.OffreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/offres")
public class OffreController {
    private final OffreService service;

    public OffreController(OffreService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OffreResponse> create(@Valid @RequestBody CreateOffreRequest request) {
        OffreResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/offres/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public OffreResponse update(
            @PathVariable Long id, @Valid @RequestBody UpdateOffreRequest request) {
        return service.update(id, request);
    }

    @GetMapping("/{id}")
    public OffreDetailResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public OffrePageResponse search(
            @RequestParam(required = false) OfferNiche niche,
            @RequestParam(required = false) Long advertiserId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return service.search(niche, advertiserId, tag, keyword, page, size, sort);
    }
}
