package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entity.*;
import java.time.Instant;

public record PublisherResponse(
        Long id,
        String email,
        ProfileStatus status,
        Instant createdAt,
        String firstName,
        String lastName,
        Niche niche,
        String bio,
        String country) {}
