package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignResponse(
        Long id,
        Long offreId,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal budget,
        PayoutModel payoutModel,
        BigDecimal payoutValue,
        CampaignStatus status) {}
