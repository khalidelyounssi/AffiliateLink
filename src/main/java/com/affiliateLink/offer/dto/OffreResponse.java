package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.OfferNiche;
import java.time.Instant;
import java.util.*;

public record OffreResponse(
        Long id,
        Long advertiserId,
        String title,
        String description,
        String landingUrl,
        OfferNiche niche,
        Instant createdAt,
        Set<String> tags) {}
