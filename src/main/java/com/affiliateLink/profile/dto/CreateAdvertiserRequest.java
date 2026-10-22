package com.affiliateLink.profile.dto;

import jakarta.validation.constraints.*;

public record CreateAdvertiserRequest(
        @NotBlank(message = "L’email est obligatoire")
        @Email(message = "L’email est invalide")
        @Size(max = 254, message = "Maximum 254 caractères")
        String email,

        @NotBlank(message = "La raison sociale est obligatoire")
        @Size(max = 200, message = "Maximum 200 caractères")
        String companyName,

        @NotBlank(message = "Le secteur est obligatoire")
        @Size(max = 100, message = "Maximum 100 caractères")
        String sector,

        @NotBlank(message = "Le site web est obligatoire")
        @Size(max = 500, message = "Maximum 500 caractères")
        String website
) {}