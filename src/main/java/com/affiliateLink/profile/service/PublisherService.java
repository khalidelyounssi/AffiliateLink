package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.*;
import com.affiliateLink.profile.dto.*;
import com.affiliateLink.profile.entity.Publisher;
import com.affiliateLink.profile.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublisherService {
    private final PublisherRepository repository;
    private final ProfileRepository profileRepository;

    public PublisherService(PublisherRepository repository,
                            ProfileRepository profileRepository) {
        this.repository = repository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public PublisherResponse create(CreatePublisherRequest request) {
        if (profileRepository.existsByEmailIgnoreCase(request.email().strip())) {
            throw new BusinessConflictException("Cet email est déjà utilisé");
        }
        Publisher publisher = new Publisher(
                request.email(), request.firstName(), request.lastName(),
                request.niche(), request.bio(), request.country()
        );
        Publisher saved = repository.saveAndFlush(publisher);
        return PublisherResponse.from(saved);
    }
    @Transactional(readOnly = true)
    public PublisherResponse findById(Long id) {
        Publisher publisher = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher introuvable"));
        return PublisherResponse.from(publisher);
    }

}