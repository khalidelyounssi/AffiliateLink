package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.CampaignStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeCampaignStatusRequest(@NotNull CampaignStatus status) {}
