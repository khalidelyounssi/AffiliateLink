package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.BusinessConflictException;
import com.affiliateLink.profile.dto.AdvertiserResponse;
import com.affiliateLink.profile.dto.CreateAdvertiserRequest;
import com.affiliateLink.profile.entity.Advertiser;
import com.affiliateLink.profile.entity.ProfileStatus;
import com.affiliateLink.profile.mapper.AdvertiserMapper;
import com.affiliateLink.profile.repository.AdvertiserRepository;
import com.affiliateLink.profile.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertiserServiceTest {

    @Mock AdvertiserRepository repository;
    @Mock ProfileRepository profiles;
    @Mock AdvertiserMapper mapper;
    @InjectMocks AdvertiserService service;

    private final CreateAdvertiserRequest request = new CreateAdvertiserRequest(
            "brand@example.test", "Brand", "Tech", "https://example.test");

    @Test
    void newAdvertiserIsSavedInPendingStatus() {
        Advertiser advertiser = new Advertiser(
                request.email(), request.companyName(), request.sector(), request.website());
        var expected = new AdvertiserResponse(7L, request.email(), ProfileStatus.PENDING,
                Instant.EPOCH, request.companyName(), request.sector(), request.website());

        when(profiles.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(advertiser);
        when(repository.saveAndFlush(advertiser)).thenReturn(advertiser);
        when(mapper.toResponse(advertiser)).thenReturn(expected);

        var response = service.create(request);

        verify(repository).saveAndFlush(argThat(saved -> saved.getStatus() == ProfileStatus.PENDING));
        assertThat(response.status()).isEqualTo(ProfileStatus.PENDING);
    }

    @Test
    void duplicateEmailIsRejectedWithoutSaving() {
        when(profiles.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessConflictException.class)
                .hasMessage("Cet email est déjà utilisé");

        verifyNoInteractions(repository, mapper);
    }
}