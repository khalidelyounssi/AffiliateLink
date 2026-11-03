package com.affiliateLink.offer.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@Profile("demo-mail")
public class DemoPublisherRecipients implements PublisherRecipients {
    private final String configured;

    public DemoPublisherRecipients(@Value("${app.demo.publisher-emails:}") String configured) {
        this.configured = configured;
    }

    @Override
    public List<String> forAdvertiser(Long advertiserId) {
        List<String> result = new ArrayList<>();
        for (String email : configured.split(",")) {
            if (!email.isBlank()) {
                result.add(email.strip());
            }
        }
        return result;
    }
}
