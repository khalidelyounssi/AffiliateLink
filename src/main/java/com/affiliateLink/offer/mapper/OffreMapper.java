package com.affiliateLink.offer.mapper;

import com.affiliateLink.offer.dto.*;
import com.affiliateLink.offer.entity.*;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import java.util.*;

@Mapper(componentModel = "spring", uses = CampaignMapper.class)
public interface OffreMapper {
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    Offre toEntity(CreateOffreRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "landingUrl", source = "landingUrl")
    @Mapping(target = "niche", source = "niche")
    void update(UpdateOffreRequest request, @MappingTarget Offre offre);

    OffreResponse toResponse(Offre offre);

    OffreDetailResponse toDetail(Offre offre);

    OffrePageResponse toPage(Page<Offre> page);

    default Set<String> tagNames(Set<Tag> tags) {
        Set<String> names = new TreeSet<>();
        for (Tag tag : tags) {
            names.add(tag.getName());
        }
        return names;
    }
}
