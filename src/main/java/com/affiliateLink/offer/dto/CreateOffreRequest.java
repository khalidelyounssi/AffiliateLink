package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.OfferNiche;
import jakarta.validation.constraints.*;
import java.util.Set;

public record CreateOffreRequest(
        @NotNull @Positive Long advertiserId,
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 4000) String description,
        @NotBlank
        @Size(max = 1000)
        @Pattern(regexp = "https?://[^\\s]+", message = "URL HTTP ou HTTPS attendue")
        String landingUrl,
        @NotNull OfferNiche niche,
        @NotNull @Size(max = 20) Set<@NotBlank @Size(max = 50) String> tags) {}
