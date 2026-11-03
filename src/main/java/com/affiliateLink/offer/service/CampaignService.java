package com.affiliateLink.offer.service;

import com.affiliateLink.common.exception.*;
import com.affiliateLink.profile.ProfileApi;
import com.affiliateLink.offer.dto.*;
import com.affiliateLink.offer.entity.*;
import com.affiliateLink.offer.mapper.CampaignMapper;
import com.affiliateLink.offer.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CampaignService {
    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);
    private final CampaignRepository campaigns;
    private final OffreRepository offres;
    private final ProfileApi profiles;
    private final CampaignMapper mapper;

    public CampaignService(
            CampaignRepository campaigns,
            OffreRepository offres,
            ProfileApi profiles,
            CampaignMapper mapper) {
        this.campaigns = campaigns;
        this.offres = offres;
        this.profiles = profiles;
        this.mapper = mapper;
    }

    @Transactional
    public CampaignResponse create(Long offreId, CreateCampaignRequest request) {
        Long owner =
                offres.findAdvertiserId(offreId)
                        .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable"));
        profiles.requireActiveAdvertiser(owner);
        Offre offre =
                offres.findLockedById(offreId)
                        .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable"));
        validate(request);
        Campaign campaign = mapper.toEntity(request);
        campaign.attachTo(offre);
        Campaign saved = campaigns.saveAndFlush(campaign);
        log.info("Campaign {} created for offre {}", saved.getId(), offreId);
        return mapper.toResponse(saved);
    }

    private void validate(CreateCampaignRequest request) {
        if (!request.endDate().isAfter(request.startDate())) {
            throw new InputValidationException("endDate", "Doit être après startDate");
        }
        if (request.payoutModel() == PayoutModel.CPA_PERCENT) {
            if (request.payoutValue().compareTo(BigDecimal.ZERO) < 0
                    || request.payoutValue().compareTo(new BigDecimal("100")) > 0) {
                throw new InputValidationException("payoutValue", "Pourcentage entre 0 et 100");
            }
        } else if (request.payoutValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InputValidationException("payoutValue", "Doit être strictement positif");
        }
    }

    @Transactional
    public void changeStatus(Long id, CampaignStatus next) {
        Long offreId =
                campaigns
                        .findOffreId(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Campagne introuvable"));
        Long owner =
                offres.findAdvertiserId(offreId)
                        .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable"));
        if (next == CampaignStatus.ACTIVE) {
            profiles.requireActiveAdvertiser(owner);
        }
        offres.findLockedById(offreId)
                .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable"));
        Campaign target =
                campaigns
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Campagne introuvable"));
        LocalDate today = LocalDate.now();
        target.checkTransition(next, today);
        if (next == CampaignStatus.ACTIVE) {
            for (Campaign old :
                    campaigns.findByOffreIdAndStatusOrderByIdAsc(offreId, CampaignStatus.ACTIVE)) {
                old.changeStatus(CampaignStatus.PAUSED, today);
            }
            campaigns.flush();
        }
        target.changeStatus(next, today);
        campaigns.flush();
        log.info("Campaign {} changed to {}", id, next);
    }
}
