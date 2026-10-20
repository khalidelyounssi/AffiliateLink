package com.affiliateLink.profile.entity;

import com.affiliateLink.common.exception.BusinessConflictException;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Locale;

@Entity
@Table(name = "profile_profiles")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProfileStatus status = ProfileStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Profile() {
    }

    protected Profile(String email) {
        this.email = email.strip().toLowerCase(Locale.ROOT);
    }

    @PrePersist
    protected void beforeInsert() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void changeStatus(ProfileStatus newStatus) {
        boolean allowed =
                (status == ProfileStatus.PENDING && newStatus == ProfileStatus.ACTIVE)
                        || (status == ProfileStatus.ACTIVE && newStatus == ProfileStatus.SUSPENDED)
                        || (status == ProfileStatus.SUSPENDED && newStatus == ProfileStatus.ACTIVE);

        if (!allowed) {
            throw new BusinessConflictException(
                    "Transition interdite : " + status + " -> " + newStatus
            );
        }
        this.status = newStatus;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public ProfileStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}