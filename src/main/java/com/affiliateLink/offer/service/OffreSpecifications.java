package com.affiliateLink.offer.service;

import com.affiliateLink.offer.entity.*;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.*;

public final class OffreSpecifications {
    private OffreSpecifications() {}

    public static Specification<Offre> filter(
            OfferNiche niche, Long advertiserId, String tag, String keyword) {
        return (root, query, cb) -> {
            List<Predicate> rules = new ArrayList<>();
            if (niche != null) {
                rules.add(cb.equal(root.get("niche"), niche));
            }
            if (advertiserId != null) {
                rules.add(cb.equal(root.get("advertiserId"), advertiserId));
            }
            if (tag != null && !tag.isBlank()) {
                rules.add(
                        cb.equal(
                                root.join("tags").get("name"),
                                tag.strip().toLowerCase(Locale.ROOT)));
                query.distinct(true);
            }
            if (keyword != null && !keyword.isBlank()) {
                String value =
                        keyword.strip()
                                .toLowerCase(Locale.ROOT)
                                .replace("!", "!!")
                                .replace("%", "!%")
                                .replace("_", "!_");
                rules.add(cb.like(cb.lower(root.get("title")), "%" + value + "%", '!'));
            }
            return cb.and(rules.toArray(new Predicate[0]));
        };
    }
}
