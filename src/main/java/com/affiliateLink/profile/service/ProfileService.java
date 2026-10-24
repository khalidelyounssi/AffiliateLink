package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.entity.Profile;
import com.affiliateLink.profile.entity.ProfileStatus;
import com.affiliateLink.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void changeStatus(Long id, ProfileStatus status) {
        Profile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profil introuvable"));
        profile.changeStatus(status);
        repository.saveAndFlush(profile);
    }
}