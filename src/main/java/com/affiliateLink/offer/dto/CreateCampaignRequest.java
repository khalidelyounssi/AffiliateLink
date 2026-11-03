package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.PayoutModel;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCampaignRequest(
        @NotBlank @Size(max = 200) String name,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 17, fraction = 2)
        BigDecimal budget,
        @NotNull PayoutModel payoutModel,
        @NotNull @DecimalMin("0") @Digits(integer = 15, fraction = 4) BigDecimal payoutValue) {}
