package com.affiliateLink.offer;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignSnapshot(
        Long id,
        Long offreId,
        Long advertiserId,
        String status,
        String payoutModel,
        BigDecimal payoutValue,
        LocalDate startDate,
        LocalDate endDate) {}
