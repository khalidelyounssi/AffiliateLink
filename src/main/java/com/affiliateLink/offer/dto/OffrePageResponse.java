package com.affiliateLink.offer.dto;

import java.util.List;

public record OffrePageResponse(
        List<OffreResponse> content, int number, int size, long totalElements, int totalPages) {}
