package com.affiliateLink.offer.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "offer_offres")
public class Offre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advertiser_id", nullable = false)
    private Long advertiserId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(name = "landing_url", nullable = false, length = 1000)
    private String landingUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OfferNiche niche;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "offre")
    @OrderBy("id ASC")
    private List<Campaign> campaigns = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "offer_offre_tags",
            joinColumns = @JoinColumn(name = "offre_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    protected Offre() {}

    public Offre(
            Long advertiserId,
            String title,
            String description,
            String landingUrl,
            OfferNiche niche) {
        this.advertiserId = advertiserId;
        this.title = title;
        this.description = description;
        this.landingUrl = landingUrl;
        this.niche = niche;
    }

    @PrePersist
    void beforeInsert() {
        createdAt = Instant.now();
    }

    public void replaceTags(Set<Tag> newTags) {
        for (Tag old : tags) {
            old.getOffres().remove(this);
        }
        tags.clear();
        for (Tag tag : newTags) {
            tags.add(tag);
            tag.getOffres().add(this);
        }
    }

    public void addCampaign(Campaign campaign) {
        campaigns.add(campaign);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLandingUrl(String landingUrl) {
        this.landingUrl = landingUrl;
    }

    public void setNiche(OfferNiche niche) {
        this.niche = niche;
    }

    public Long getId() {
        return id;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLandingUrl() {
        return landingUrl;
    }

    public OfferNiche getNiche() {
        return niche;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }
}
