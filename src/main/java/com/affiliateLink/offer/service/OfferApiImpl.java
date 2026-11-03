package com.affiliateLink.offer.service;

import com.affiliateLink.offer.*;
import com.affiliateLink.offer.mapper.CampaignMapper;
import com.affiliateLink.offer.repository.CampaignRepository;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfferApiImpl implements OfferApi {
    private final CampaignRepository repository;
    private final CampaignMapper mapper;

    public OfferApiImpl(CampaignRepository repository, CampaignMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignSnapshot getCampaign(Long id) {
        return mapper.toSnapshot(
                repository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Campagne introuvable")));
    }
}
