package com.affiliateLink.offer.entity;

import com.affiliateLink.common.exception.BusinessConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.affiliateLink.offer.entity.CampaignStatus.*;
import static org.assertj.core.api.Assertions.*;

class CampaignTest {
    private static final LocalDate TODAY = LocalDate.of(2030, 1, 15);

    @ParameterizedTest(name = "{0} -> {1}, autorisée = {2}")
    @MethodSource("transitions")
    void verifiesEveryTransition(CampaignStatus from, CampaignStatus to, boolean allowed) {
        Campaign campaign = campaignIn(from);

        if (allowed) {
            campaign.changeStatus(to, TODAY);
            assertThat(campaign.getStatus()).isEqualTo(to);
        } else {
            assertThatThrownBy(() -> campaign.changeStatus(to, TODAY))
                    .isInstanceOf(BusinessConflictException.class);
            assertThat(campaign.getStatus()).isEqualTo(from);
        }
    }

    static Stream<Arguments> transitions() {
        Map<CampaignStatus, Set<CampaignStatus>> expected = Map.of(
                DRAFT, Set.of(ACTIVE),
                ACTIVE, Set.of(PAUSED, ENDED),
                PAUSED, Set.of(ACTIVE, ENDED),
                ENDED, Set.of());

        return Arrays.stream(CampaignStatus.values()).flatMap(from ->
                Arrays.stream(CampaignStatus.values()).map(to ->
                        Arguments.of(from, to, expected.get(from).contains(to))));
    }

    @Test
    void expiredCampaignCannotBeActivated() {
        Campaign campaign = new Campaign("Expirée", TODAY.minusDays(10), TODAY.minusDays(1),
                new BigDecimal("100"), PayoutModel.CPC, new BigDecimal("1"));

        assertThatThrownBy(() -> campaign.changeStatus(ACTIVE, TODAY))
                .isInstanceOf(BusinessConflictException.class)
                .hasMessage("Campagne expirée");
        assertThat(campaign.getStatus()).isEqualTo(DRAFT);
    }

    @Test
    void campaignCanBeActivatedOnItsEndDate() {
        Campaign campaign = new Campaign("Dernier jour", TODAY.minusDays(1), TODAY,
                new BigDecimal("100"), PayoutModel.CPC, new BigDecimal("1"));

        campaign.changeStatus(ACTIVE, TODAY);

        assertThat(campaign.getStatus()).isEqualTo(ACTIVE);
    }

    private Campaign campaignIn(CampaignStatus status) {
        Campaign campaign = new Campaign("Campagne", TODAY.minusDays(1), TODAY.plusDays(10),
                new BigDecimal("100"), PayoutModel.CPC, new BigDecimal("1"));
        if (status != DRAFT) {
            campaign.changeStatus(ACTIVE, TODAY);
        }
        if (status == PAUSED || status == ENDED) {
            campaign.changeStatus(status, TODAY);
        }
        return campaign;
    }
}