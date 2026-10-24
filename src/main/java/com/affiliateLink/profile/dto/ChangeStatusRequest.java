package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entity.ProfileStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
        @NotNull(message = "Le statut est obligatoire") ProfileStatus status
) {}