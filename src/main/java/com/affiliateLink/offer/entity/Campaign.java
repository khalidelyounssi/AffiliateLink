package com.affiliateLink.offer.entity;

import com.affiliateLink.common.exception.BusinessConflictException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "offer_campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_model", nullable = false, length = 20)
    private PayoutModel payoutModel;

    @Column(name = "payout_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal payoutValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status = CampaignStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;

    protected Campaign() {}

    public Campaign(
            String name,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal budget,
            PayoutModel payoutModel,
            BigDecimal payoutValue) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.payoutModel = payoutModel;
        this.payoutValue = payoutValue;
    }

    public void attachTo(Offre offre) {
        this.offre = offre;
        offre.addCampaign(this);
    }

    public void checkTransition(CampaignStatus next, LocalDate today) {
        boolean allowed = false;

        if (status == CampaignStatus.DRAFT) {
            allowed = next == CampaignStatus.ACTIVE;
        } else if (status == CampaignStatus.ACTIVE) {
            allowed = next == CampaignStatus.PAUSED || next == CampaignStatus.ENDED;
        } else if (status == CampaignStatus.PAUSED) {
            allowed = next == CampaignStatus.ACTIVE || next == CampaignStatus.ENDED;
        }

        if (!allowed) {
            throw new BusinessConflictException("Transition interdite : " + status + " -> " + next);
        }
        if (next == CampaignStatus.ACTIVE && endDate.isBefore(today)) {
            throw new BusinessConflictException("Campagne expirée");
        }
    }

    public void changeStatus(CampaignStatus next, LocalDate today) {
        checkTransition(next, today);
        this.status = next;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public PayoutModel getPayoutModel() {
        return payoutModel;
    }

    public BigDecimal getPayoutValue() {
        return payoutValue;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public Offre getOffre() {
        return offre;
    }
}
