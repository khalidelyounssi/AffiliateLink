package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.ProfileSuspended;
import com.affiliateLink.profile.entity.*;
import com.affiliateLink.profile.repository.ProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProfileService {
    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);
    private final ProfileRepository repository;
    private final ApplicationEventPublisher events;

    public ProfileService(ProfileRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional
    public void changeStatus(Long id, ProfileStatus status) {
        Profile profile =
                repository
                        .findLockedById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Profil introuvable"));
        profile.changeStatus(status);
        repository.saveAndFlush(profile);
        if (status == ProfileStatus.SUSPENDED) {
            events.publishEvent(new ProfileSuspended(id));
        }
        log.info("Profile {} changed to {}", id, status);
    }
}
