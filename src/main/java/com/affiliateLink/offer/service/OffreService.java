package com.affiliateLink.offer.service;

import com.affiliateLink.common.exception.*;
import com.affiliateLink.profile.ProfileApi;
import com.affiliateLink.offer.dto.*;
import com.affiliateLink.offer.entity.*;
import com.affiliateLink.offer.mapper.OffreMapper;
import com.affiliateLink.offer.repository.OffreRepository;
import org.springframework.data.domain.*;
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

    @Transactional
    public OffreResponse update(Long id, UpdateOffreRequest request) {
        Offre offre =
                repository
                        .findLockedById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable"));
        mapper.update(request, offre);
        Set<Tag> resolvedTags = tags.resolve(request.tags());
        offre.replaceTags(resolvedTags);
        repository.flush();
        log.info("Offre {} updated", id);
        return mapper.toResponse(offre);
    }

    @Transactional(readOnly = true)
    public OffrePageResponse search(
            OfferNiche niche,
            Long advertiserId,
            String tag,
            String keyword,
            int page,
            int size,
            String sort) {
        if (page < 0) {
            throw new InputValidationException("page", "Doit être >= 0");
        }
        if (size < 1 || size > 100) {
            throw new InputValidationException("size", "Entre 1 et 100");
        }
        if (advertiserId != null && advertiserId < 1) {
            throw new InputValidationException("advertiserId", "Doit être positif");
        }
        String[] parts = sort.split(",", -1);
        Set<String> allowed = Set.of("id", "title", "createdAt");
        if (parts.length != 2
                || !allowed.contains(parts[0])
                || !(parts[1].equalsIgnoreCase("asc") || parts[1].equalsIgnoreCase("desc"))) {
            throw new InputValidationException(
                    "sort", "Format attendu : id,asc ou title,desc ou createdAt,desc");
        }
        Sort ordering = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
        if (!parts[0].equals("id")) {
            ordering = ordering.and(Sort.by("id"));
        }
        Pageable pageable = PageRequest.of(page, size, ordering);
        log.debug("Searching offres: page={}, size={}", page, size);
        return mapper.toPage(
                repository.findAll(
                        OffreSpecifications.filter(niche, advertiserId, tag, keyword), pageable));
    }
}
