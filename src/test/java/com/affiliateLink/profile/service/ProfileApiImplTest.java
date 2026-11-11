package com.affiliateLink.profile.service;

import com.affiliateLink.common.exception.BusinessConflictException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.profile.entity.Advertiser;
import com.affiliateLink.profile.entity.ProfileStatus;
import com.affiliateLink.profile.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileApiImplTest {
    @Mock ProfileRepository repository;
    @InjectMocks ProfileApiImpl api;

    @Test
    void missingAdvertiserIsRejected() {
        when(repository.findLockedById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> api.requireActiveAdvertiser(7L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void pendingAdvertiserIsRejected() {
        var pending = new Advertiser("brand@example.test", "Brand", "Tech", "https://example.test");
        when(repository.findLockedById(7L)).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> api.requireActiveAdvertiser(7L))
                .isInstanceOf(BusinessConflictException.class);
    }

    @Test
    void activeAdvertiserIsAccepted() {
        var active = new Advertiser("brand@example.test", "Brand", "Tech", "https://example.test");
        active.changeStatus(ProfileStatus.ACTIVE);
        when(repository.findLockedById(7L)).thenReturn(Optional.of(active));

        assertThatCode(() -> api.requireActiveAdvertiser(7L)).doesNotThrowAnyException();
    }
}