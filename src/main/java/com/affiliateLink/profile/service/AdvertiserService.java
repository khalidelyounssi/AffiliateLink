package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.*;
import com.affiliateLink.profile.dto.*;
import com.affiliateLink.profile.entity.Advertiser;
import com.affiliateLink.profile.mapper.AdvertiserMapper;
import com.affiliateLink.profile.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdvertiserService {
    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(AdvertiserService.class);

    private final AdvertiserRepository repository;
    private final ProfileRepository profileRepository;
    private final AdvertiserMapper mapper;

    public AdvertiserService(
            AdvertiserRepository repository,
            ProfileRepository profileRepository,
            AdvertiserMapper mapper) {
        this.repository = repository;
        this.profileRepository = profileRepository;
        this.mapper = mapper;
    }

    @Transactional
    public AdvertiserResponse create(CreateAdvertiserRequest request) {
        if (profileRepository.existsByEmailIgnoreCase(request.email().strip())) {
            throw new BusinessConflictException("Cet email est déjà utilisé");
        }
        Advertiser entity = mapper.toEntity(request);
        Advertiser saved = repository.saveAndFlush(entity);
        log.info("Advertiser {} created", saved.getId());
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AdvertiserResponse findById(Long id) {
        return mapper.toResponse(
                repository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Advertiser introuvable")));
    }

    @Transactional(readOnly = true)
    public List<AdvertiserResponse> findAll() {
        return mapper.toResponses(repository.findAll());
    }
}
