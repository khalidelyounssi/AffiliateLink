package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.*;
import com.affiliateLink.profile.dto.*;
import com.affiliateLink.profile.entity.Publisher;
import com.affiliateLink.profile.mapper.PublisherMapper;
import com.affiliateLink.profile.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PublisherService {
    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(PublisherService.class);
    private final PublisherRepository repository;
    private final ProfileRepository profileRepository;
    private final PublisherMapper mapper;

    public PublisherService(
            PublisherRepository repository,
            ProfileRepository profileRepository,
            PublisherMapper mapper) {
        this.repository = repository;
        this.profileRepository = profileRepository;
        this.mapper = mapper;
    }

    @Transactional
    public PublisherResponse create(CreatePublisherRequest request) {
        if (profileRepository.existsByEmailIgnoreCase(request.email().strip())) {
            throw new BusinessConflictException("Cet email est déjà utilisé");
        }
        Publisher entity = mapper.toEntity(request);
        Publisher saved = repository.saveAndFlush(entity);
        log.info("Publisher {} created", saved.getId());
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PublisherResponse findById(Long id) {
        return mapper.toResponse(
                repository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Publisher introuvable")));
    }

    @Transactional(readOnly = true)
    public List<PublisherResponse> findAll() {
        return mapper.toResponses(repository.findAll());
    }
}
