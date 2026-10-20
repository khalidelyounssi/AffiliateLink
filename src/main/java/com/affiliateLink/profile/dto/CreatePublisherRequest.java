package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entity.Niche;
import jakarta.validation.constraints.*;

public record CreatePublisherRequest(
        @NotBlank(message = "L’email est obligatoire")
        @Email(message = "L’email est invalide")
        @Size(max = 254, message = "Maximum 254 caractères")
        String email,

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 100, message = "Maximum 100 caractères")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Maximum 100 caractères")
        String lastName,

        @NotNull(message = "La niche est obligatoire")
        Niche niche,

        @Size(max = 1000, message = "Maximum 1000 caractères")
        String bio,

        @NotBlank(message = "Le pays est obligatoire")
        @Size(max = 100, message = "Maximum 100 caractères")
        String country
) {}