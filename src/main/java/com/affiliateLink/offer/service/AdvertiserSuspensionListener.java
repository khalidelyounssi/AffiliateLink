package com.affiliateLink.offer.service;

import com.affiliateLink.profile.ProfileSuspended;
import com.affiliateLink.offer.entity.*;
import com.affiliateLink.offer.repository.*;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.slf4j.*;
import java.time.LocalDate;

@Component
public class AdvertiserSuspensionListener {
    private static final Logger log = LoggerFactory.getLogger(AdvertiserSuspensionListener.class);
    private final OffreRepository offres;
    private final CampaignRepository campaigns;

    public AdvertiserSuspensionListener(OffreRepository offres, CampaignRepository campaigns) {
        this.offres = offres;
        this.campaigns = campaigns;
    }

    @ApplicationModuleListener
    public void on(ProfileSuspended event) {
        int paused = 0;
        for (Offre candidate : offres.findByAdvertiserIdOrderByIdAsc(event.profileId())) {
            offres.findLockedById(candidate.getId()).orElseThrow();
            for (Campaign campaign :
                    campaigns.findByOffreIdAndStatusOrderByIdAsc(
                            candidate.getId(), CampaignStatus.ACTIVE)) {
                campaign.changeStatus(CampaignStatus.PAUSED, LocalDate.now());
                paused++;
            }
        }
        campaigns.flush();
        log.info("Suspension {}: {} campaigns paused", event.profileId(), paused);
    }
}
