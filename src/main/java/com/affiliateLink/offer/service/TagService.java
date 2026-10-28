package com.affiliateLink.offer.service;

import com.affiliateLink.offer.entity.Tag;
import com.affiliateLink.offer.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class TagService {
    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Set<Tag> resolve(Set<String> rawNames) {
        Set<String> normalized = new TreeSet<>();
        for (String raw : rawNames) {
            normalized.add(raw.strip().toLowerCase(Locale.ROOT));
        }
        Set<Tag> tags = new HashSet<>();
        for (String name : normalized) {
            repository.insertIfMissing(name);
            tags.add(repository.findByName(name).orElseThrow());
        }
        return tags;
    }
}
