package com.affiliateLink.profile.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profile_advertisers")
@PrimaryKeyJoinColumn(name = "id")
public class Advertiser extends Profile {
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(nullable = false, length = 100)
    private String sector;

    @Column(nullable = false, length = 500)
    private String website;

    protected Advertiser() {
    }

    public Advertiser(String email, String companyName,
                      String sector, String website) {
        super(email);
        this.companyName = companyName;
        this.sector = sector;
        this.website = website;
    }

    public String getCompanyName() { return companyName; }
    public String getSector() { return sector; }
    public String getWebsite() { return website; }
}