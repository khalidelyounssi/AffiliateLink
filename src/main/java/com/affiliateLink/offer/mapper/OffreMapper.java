package com.affiliateLink.offer.mapper;

import com.affiliateLink.offer.dto.*;
import com.affiliateLink.offer.entity.*;
import org.mapstruct.*;
import java.util.*;

@Mapper(componentModel = "spring")
public interface OffreMapper {
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    Offre toEntity(CreateOffreRequest request);

    OffreResponse toResponse(Offre offre);

    default Set<String> tagNames(Set<Tag> tags) {
        Set<String> names = new TreeSet<>();
        for (Tag tag : tags) {
            names.add(tag.getName());
        }
        return names;
    }
}
