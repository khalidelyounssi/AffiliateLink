package com.affiliateLink.offer.mapper;

import com.affiliateLink.offer.dto.*;
import com.affiliateLink.offer.entity.Campaign;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignMapper {
    @Mapping(target = "offreId", source = "offre.id")
    CampaignResponse toResponse(Campaign campaign);

    List<CampaignResponse> toResponses(List<Campaign> campaigns);
}
