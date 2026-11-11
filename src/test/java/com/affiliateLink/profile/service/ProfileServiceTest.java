package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.ProfileSuspended;
import com.affiliateLink.profile.entity.Advertiser;
import com.affiliateLink.profile.entity.ProfileStatus;
import com.affiliateLink.profile.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock ProfileRepository repository;
    @Mock ApplicationEventPublisher events;
    @InjectMocks ProfileService service;

    @Test
    void suspendingActiveProfileSavesAndPublishesEvent() {
        // Given
        Advertiser advertiser = advertiser();
        advertiser.changeStatus(ProfileStatus.ACTIVE);
        when(repository.findLockedById(7L)).thenReturn(Optional.of(advertiser));

        // When
        service.changeStatus(7L, ProfileStatus.SUSPENDED);

        // Then
        assertThat(advertiser.getStatus()).isEqualTo(ProfileStatus.SUSPENDED);
        var order = inOrder(repository, events);
        order.verify(repository).saveAndFlush(advertiser);
        order.verify(events).publishEvent(new ProfileSuspended(7L));
    }

    @Test
    void activatingPendingProfileDoesNotPublishAnEvent() {
        Advertiser advertiser = advertiser();
        when(repository.findLockedById(7L)).thenReturn(Optional.of(advertiser));

        service.changeStatus(7L, ProfileStatus.ACTIVE);

        assertThat(advertiser.getStatus()).isEqualTo(ProfileStatus.ACTIVE);
        verify(repository).saveAndFlush(advertiser);
        verifyNoInteractions(events);
    }

    @Test
    void missingProfileIsNeitherSavedNorPublished() {
        when(repository.findLockedById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(99L, ProfileStatus.ACTIVE))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profil introuvable");

        verify(repository, never()).saveAndFlush(any());
        verifyNoInteractions(events);
    }

    private Advertiser advertiser() {
        return new Advertiser("brand@example.test", "Brand", "Tech", "https://example.test");
    }
}