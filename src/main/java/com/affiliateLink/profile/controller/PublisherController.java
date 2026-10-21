package com.affiliateLink.profile.controller;


import com.affiliateLink.profile.dto.CreatePublisherRequest;
import com.affiliateLink.profile.dto.PublisherResponse;
import com.affiliateLink.profile.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {
    private final PublisherService service;

    public PublisherController(PublisherService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PublisherResponse> create(@Valid @RequestBody CreatePublisherRequest request){
        PublisherResponse response = service.create(request);
        return ResponseEntity.created(
                URI.create("/api/publishers/"+ response.id())
        ).body(response);
    }

}