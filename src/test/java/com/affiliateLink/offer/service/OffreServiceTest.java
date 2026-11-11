package com.affiliateLink.offer.service;

import com.affiliateLink.common.exception.BusinessConflictException;
import com.affiliateLink.common.exception.InputValidationException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.offer.dto.CreateOffreRequest;
import com.affiliateLink.offer.entity.OfferNiche;
import com.affiliateLink.offer.mapper.OffreMapper;
import com.affiliateLink.offer.repository.OffreRepository;
import com.affiliateLink.profile.ProfileApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OffreServiceTest {
    @Mock OffreRepository repository;
    @Mock ProfileApi profiles;
    @Mock OffreMapper mapper;
    @Mock TagService tags;
    @InjectMocks OffreService service;

    private final CreateOffreRequest request =
            new CreateOffreRequest(7L, "Clavier", "Description",
                    "https://example.test", OfferNiche.TECH, Set.of("java"));

    @Test
    void missingAdvertiserPreventsSavingOffer() {
        doThrow(new ResourceNotFoundException("Advertiser introuvable"))
                .when(profiles).requireActiveAdvertiser(7L);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(repository, mapper, tags);
    }

    @Test
    void inactiveAdvertiserPreventsSavingOffer() {
        doThrow(new BusinessConflictException("Advertiser non actif"))
                .when(profiles).requireActiveAdvertiser(7L);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessConflictException.class);

        verifyNoInteractions(repository, mapper, tags);
    }

    @Test
    void negativePageDoesNotQueryRepository() {
        assertThatThrownBy(() -> service.search(null, null, null, null, -1, 10, "id,asc"))
                .isInstanceOfSatisfying(InputValidationException.class,
                        ex -> assertThat(ex.getField()).isEqualTo("page"));

        verifyNoInteractions(repository);
    }
}