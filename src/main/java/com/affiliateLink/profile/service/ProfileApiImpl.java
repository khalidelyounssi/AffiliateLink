package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.BusinessConflictException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.ProfileApi;
import com.affiliateLink.profile.entity.Advertiser;
import com.affiliateLink.profile.entity.Profile;
import com.affiliateLink.profile.entity.ProfileStatus;
import com.affiliateLink.profile.repository.ProfileRepository;
import org.springframework.transaction.annotation.Transactional;

public class ProfileApiImpl implements ProfileApi {

    private final ProfileRepository profileRepository;

    public ProfileApiImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public void requireActiveAdvertiser(Long advertiserId) {

        Profile profile = profileRepository.findLockedById(advertiserId).orElseThrow(()->new ResourceNotFoundException("Advertiser introuvable"));

        if(!(profile instanceof Advertiser)){
            throw new ResourceNotFoundException("Advertiser introuvable");
        }
        if(profile.getStatus() != ProfileStatus.ACTIVE){
            throw new BusinessConflictException("Advertiser non actif");
        }



    }
}
