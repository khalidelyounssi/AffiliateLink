package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entity.*;
import java.time.Instant;

public record AdvertiserResponse(
        Long id,
        String email,
        ProfileStatus status,
        Instant createdAt,
        String companyName,
        String sector,
        String website) {}
