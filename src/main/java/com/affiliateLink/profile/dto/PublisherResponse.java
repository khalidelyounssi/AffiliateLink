package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entity.*;
import java.time.Instant;

public record PublisherResponse(
        Long id, String email, ProfileStatus status, Instant createdAt,
        String firstName, String lastName, Niche niche, String bio, String country
) {
    public static PublisherResponse from(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(), publisher.getEmail(), publisher.getStatus(),
                publisher.getCreatedAt(), publisher.getFirstName(),
                publisher.getLastName(), publisher.getNiche(),
                publisher.getBio(), publisher.getCountry()
        );
    }
}