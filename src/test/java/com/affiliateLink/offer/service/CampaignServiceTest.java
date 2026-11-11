package com.affiliateLink.offer.service;

import com.affiliateLink.common.exception.InputValidationException;
import com.affiliateLink.offer.dto.CampaignResponse;
import com.affiliateLink.offer.dto.CreateCampaignRequest;
import com.affiliateLink.offer.entity.*;
import com.affiliateLink.offer.mapper.CampaignMapper;
import com.affiliateLink.offer.repository.CampaignRepository;
import com.affiliateLink.offer.repository.OffreRepository;
import com.affiliateLink.profile.ProfileApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.affiliateLink.offer.entity.CampaignStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {
    @Mock CampaignRepository campaigns;
    @Mock OffreRepository offres;
    @Mock ProfileApi profiles;
    @Mock CampaignMapper mapper;
    @InjectMocks CampaignService service;

    private final Offre offre = new Offre(7L, "Clavier", "Description",
            "https://example.test", OfferNiche.TECH);

    @ParameterizedTest
    @CsvSource({"CPC,0", "CPC,-1", "CPL,0", "CPL,-1", "CPA_FIXED,0",
            "CPA_FIXED,-1", "CPA_PERCENT,-0.01", "CPA_PERCENT,100.01"})
    void rejectsInvalidPayout(PayoutModel model, String amount) {
        offerExists();
        var request = request(model, amount, 1);

        assertInvalidField(request, "payoutValue");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsEndDateNotAfterStartDate(int endOffset) {
        offerExists();

        assertInvalidField(request(PayoutModel.CPC, "1", endOffset), "endDate");
    }

    @ParameterizedTest
    @CsvSource({"CPC,0.01", "CPL,1", "CPA_FIXED,5", "CPA_PERCENT,0", "CPA_PERCENT,100"})
    void acceptsValidPayout(PayoutModel model, String amount) {
        offerExists();
        var request = request(model, amount, 1);
        var entity = new Campaign(request.name(), request.startDate(), request.endDate(),
                request.budget(), request.payoutModel(), request.payoutValue());
        var expected = new CampaignResponse(21L, 11L, request.name(),
                request.startDate(), request.endDate(), request.budget(), model, request.payoutValue(), DRAFT);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(campaigns.saveAndFlush(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(expected);

        var response = service.create(11L, request);

        assertThat(response).isEqualTo(expected);
        assertThat(entity.getOffre()).isSameAs(offre);
        verify(campaigns).saveAndFlush(entity);
    }

    @Test
    void activatingAnotherCampaignPausesPreviousOne() {
        offerExists();
        LocalDate today = LocalDate.now();
        Campaign previous = new Campaign("Avant", today.minusDays(1), today.plusDays(10),
                BigDecimal.TEN, PayoutModel.CPC, BigDecimal.ONE);
        previous.changeStatus(ACTIVE, today);
        Campaign target = new Campaign("Après", today.minusDays(1), today.plusDays(10),
                BigDecimal.TEN, PayoutModel.CPC, BigDecimal.ONE);

        when(campaigns.findOffreId(21L)).thenReturn(Optional.of(11L));
        when(campaigns.findById(21L)).thenReturn(Optional.of(target));
        when(campaigns.findByOffreIdAndStatusOrderByIdAsc(11L, ACTIVE))
                .thenReturn(List.of(previous));

        service.changeStatus(21L, ACTIVE);

        assertThat(previous.getStatus()).isEqualTo(PAUSED);
        assertThat(target.getStatus()).isEqualTo(ACTIVE);
        verify(profiles).requireActiveAdvertiser(7L);
    }

    private void offerExists() {
        when(offres.findAdvertiserId(11L)).thenReturn(Optional.of(7L));
        when(offres.findLockedById(11L)).thenReturn(Optional.of(offre));
    }

    private CreateCampaignRequest request(PayoutModel model, String amount, int endOffset) {
        LocalDate start = LocalDate.of(2030, 1, 10);
        return new CreateCampaignRequest("Campagne", start, start.plusDays(endOffset),
                BigDecimal.TEN, model, new BigDecimal(amount));
    }

    private void assertInvalidField(CreateCampaignRequest request, String field) {
        assertThatThrownBy(() -> service.create(11L, request))
                .isInstanceOfSatisfying(InputValidationException.class,
                        ex -> assertThat(ex.getField()).isEqualTo(field));
        verifyNoInteractions(campaigns, mapper);
    }
}