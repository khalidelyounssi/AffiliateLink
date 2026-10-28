package com.affiliateLink.offer.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "offer_tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_offer_tag_name", columnNames = "name"))
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Offre> offres = new HashSet<>();

    protected Tag() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Offre> getOffres() {
        return offres;
    }
}
