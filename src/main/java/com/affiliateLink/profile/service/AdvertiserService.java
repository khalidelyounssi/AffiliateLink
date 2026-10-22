package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.*;
import com.affiliateLink.profile.dto.*;
import com.affiliateLink.profile.entity.Advertiser;
import com.affiliateLink.profile.entity.Publisher;
import com.affiliateLink.profile.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdvertiserService {
    private final AdvertiserRepository repository;
    private final ProfileRepository profileRepository;

    public AdvertiserService(AdvertiserRepository repository,
                             ProfileRepository profileRepository) {
        this.repository = repository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public AdvertiserResponse create(CreateAdvertiserRequest request) {
        if (profileRepository.existsByEmailIgnoreCase(request.email().strip())) {
            throw new BusinessConflictException("Cet email est déjà utilisé");
        }
        Advertiser advertiser = new Advertiser(
                request.email(), request.companyName(),
                request.sector(), request.website()
        );
        Advertiser saved = repository.saveAndFlush(advertiser);
        return AdvertiserResponse.from(saved);
    }
    @Transactional(readOnly = true)
    public AdvertiserResponse findById(Long id) {
        Advertiser advertiser = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Advertiser introuvable"));
        return AdvertiserResponse.from(advertiser);
    }

    @Transactional(readOnly = true)
    public List<AdvertiserResponse> findAll() {
        List<AdvertiserResponse> responses = new ArrayList<>();
        for (Advertiser advertiser : repository.findAll()) {
            responses.add(AdvertiserResponse.from(advertiser));
        }
        return responses;
    }

}