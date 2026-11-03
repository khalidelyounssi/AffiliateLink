package com.affiliateLink.offer.notification;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Profile("!demo-mail")
public class UnavailablePublisherRecipients implements PublisherRecipients {
    @Override
    public List<String> forAdvertiser(Long advertiserId) {
        throw new IllegalStateException(
                "Source des publishers concernés non configurée : confirmer le contrat Affiliate");
    }
}
