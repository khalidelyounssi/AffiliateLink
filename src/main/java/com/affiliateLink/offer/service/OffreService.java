package com.affiliateLink.offer.service;

import com.affiliateLink.common.exception.*;
import com.affiliateLink.profile.ProfileApi;
import com.affiliateLink.offer.dto.*;
import com.affiliateLink.offer.entity.*;
import com.affiliateLink.offer.mapper.OffreMapper;
import com.affiliateLink.offer.repository.OffreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.*;
import java.util.Set;

@Service
public class OffreService {
    private static final Logger log = LoggerFactory.getLogger(OffreService.class);
    private final OffreRepository repository;
    private final ProfileApi profiles;
    private final OffreMapper mapper;
    private final TagService tags;

    public OffreService(
            OffreRepository repository, ProfileApi profiles, OffreMapper mapper, TagService tags) {
        this.repository = repository;
        this.profiles = profiles;
        this.mapper = mapper;
        this.tags = tags;
    }

    @Transactional
    public OffreResponse create(CreateOffreRequest request) {
        profiles.requireActiveAdvertiser(request.advertiserId());
        Offre offre = mapper.toEntity(request);
        Set<Tag> resolvedTags = tags.resolve(request.tags());
        offre.replaceTags(resolvedTags);
        Offre saved = repository.saveAndFlush(offre);
        log.info("Offre {} created for advertiser {}", saved.getId(), saved.getAdvertiserId());
        return mapper.toResponse(saved);
    }
}
