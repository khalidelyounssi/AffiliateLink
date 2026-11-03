package com.affiliateLink.offer.notification;

import com.affiliateLink.profile.ProfileSuspended;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.*;

@Component
public class SuspensionMailListener {
    private static final Logger log = LoggerFactory.getLogger(SuspensionMailListener.class);
    private final com.affiliateLink.offer.repository.OffreRepository offres;
    private final PublisherRecipients recipients;
    private final JavaMailSender mail;
    private final String from;

    public SuspensionMailListener(
            com.affiliateLink.offer.repository.OffreRepository offres,
            PublisherRecipients recipients,
            JavaMailSender mail,
            @Value("${app.mail.from:noreply@affiliatelink.test}") String from) {
        this.offres = offres;
        this.recipients = recipients;
        this.mail = mail;
        this.from = from;
    }

    @ApplicationModuleListener
    public void on(ProfileSuspended event) {
        if (offres.findByAdvertiserIdOrderByIdAsc(event.profileId()).isEmpty()) {
            return;
        }
        int count = 0;
        for (String recipient : recipients.forAdvertiser(event.profileId())) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(recipient);
            message.setSubject("AffiliateLink : promotion suspendue");
            message.setText(
                    "Les campagnes de l'advertiser "
                            + event.profileId()
                            + " sont suspendues. Merci d'arrêter de partager ses liens.");
            mail.send(message);
            count++;
        }
        log.info("Suspension {}: {} notifications sent", event.profileId(), count);
    }
}
