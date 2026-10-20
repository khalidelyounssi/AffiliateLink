package com.affiliateLink.profile.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profile_publishers")
@PrimaryKeyJoinColumn(name = "id")
public class Publisher extends Profile {
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Niche niche;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = false, length = 100)
    private String country;

    protected Publisher() {
    }

    public Publisher(String email, String firstName, String lastName,
                     Niche niche, String bio, String country) {
        super(email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.niche = niche;
        this.bio = bio;
        this.country = country;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Niche getNiche() { return niche; }
    public String getBio() { return bio; }
    public String getCountry() { return country; }
}