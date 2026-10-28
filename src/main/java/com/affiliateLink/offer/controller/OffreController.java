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
}
