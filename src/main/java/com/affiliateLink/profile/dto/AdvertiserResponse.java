package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entity.*;
import java.time.Instant;

public record AdvertiserResponse(
        Long id, String email, ProfileStatus status, Instant createdAt,
        String companyName, String sector, String website
) {
    public static AdvertiserResponse from(Advertiser advertiser) {
        return new AdvertiserResponse(
                advertiser.getId(), advertiser.getEmail(), advertiser.getStatus(),
                advertiser.getCreatedAt(), advertiser.getCompanyName(),
                advertiser.getSector(), advertiser.getWebsite()
        );
    }
}