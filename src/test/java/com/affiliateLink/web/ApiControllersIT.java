package com.affiliateLink.web;

import com.affiliateLink.common.exception.BusinessConflictException;
import com.affiliateLink.common.exception.InputValidationException;
import com.affiliateLink.common.exception.ResourceNotFoundException;
import com.affiliateLink.offer.controller.CampaignController;
import com.affiliateLink.offer.controller.OffreController;
import com.affiliateLink.offer.service.CampaignService;
import com.affiliateLink.offer.service.OffreService;
import com.affiliateLink.profile.controller.AdminProfileController;
import com.affiliateLink.profile.controller.AdvertiserController;
import com.affiliateLink.profile.controller.PublisherController;
import com.affiliateLink.profile.dto.AdvertiserResponse;
import com.affiliateLink.profile.dto.PublisherResponse;
import com.affiliateLink.profile.entity.Niche;
import com.affiliateLink.profile.entity.ProfileStatus;
import com.affiliateLink.profile.service.AdvertiserService;
import com.affiliateLink.profile.service.ProfileService;
import com.affiliateLink.profile.service.PublisherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({AdvertiserController.class, PublisherController.class, AdminProfileController.class,
        OffreController.class, CampaignController.class})
@ActiveProfiles("test")
class ApiControllersIT {
    private static final String ADVERTISER_JSON = """
            {"email":"brand@example.test","companyName":"Brand",
             "sector":"Tech","website":"https://example.test"}
            """;

    @Autowired MockMvc mvc;
    @MockitoBean AdvertiserService advertisers;
    @MockitoBean PublisherService publishers;
    @MockitoBean ProfileService profiles;
    @MockitoBean OffreService offres;
    @MockitoBean CampaignService campaigns;

    @Test
    void advertiserCreationReturns201AndLocation() throws Exception {
        when(advertisers.create(any())).thenReturn(new AdvertiserResponse(7L, "brand@example.test",
                ProfileStatus.PENDING, Instant.EPOCH, "Brand", "Tech", "https://example.test"));

        mvc.perform(post("/api/advertisers").contentType(MediaType.APPLICATION_JSON).content(ADVERTISER_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/advertisers/7"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void invalidAdvertiserEmailReturnsFieldError() throws Exception {
        mvc.perform(post("/api/advertisers").contentType(MediaType.APPLICATION_JSON)
                        .content(ADVERTISER_JSON.replace("brand@example.test", "invalide")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Données invalides"))
                .andExpect(jsonPath("$.detail").value("Vérifiez les champs du formulaire"))
                .andExpect(jsonPath("$.errors[0].field").value("email"));

        verifyNoInteractions(advertisers);
    }

    @Test
    void missingAdvertiserReturns404WithProblemDetail() throws Exception {
        when(advertisers.findById(99L))
                .thenThrow(new ResourceNotFoundException("Advertiser introuvable"));

        mvc.perform(get("/api/advertisers/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Ressource introuvable"))
                .andExpect(jsonPath("$.detail").value("Advertiser introuvable"));
    }

    @Test
    void duplicateEmailReturns409WithProblemDetail() throws Exception {
        when(advertisers.create(any())).thenThrow(new BusinessConflictException("Cet email est déjà utilisé"));

        mvc.perform(post("/api/advertisers").contentType(MediaType.APPLICATION_JSON).content(ADVERTISER_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflit métier"))
                .andExpect(jsonPath("$.detail").value("Cet email est déjà utilisé"));
    }

    @Test
    void publisherCanBeConsulted() throws Exception {
        when(publishers.findById(8L)).thenReturn(new PublisherResponse(8L, "pub@example.test",
                ProfileStatus.ACTIVE, Instant.EPOCH, "Nora", "Test", Niche.TECH, "Bio", "MA"));

        mvc.perform(get("/api/publishers/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("pub@example.test"));
    }

    @Test
    void adminCanRequestProfileSuspension() throws Exception {
        mvc.perform(patch("/api/admin/profiles/7/status")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"status":"SUSPENDED"}
                                """))
                .andExpect(status().isNoContent());

        verify(profiles).changeStatus(7L, ProfileStatus.SUSPENDED);
    }

    @Test
    void offerWithNonNumericIdReturns400() throws Exception {
        mvc.perform(get("/api/offres/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Paramètre invalide"))
                .andExpect(jsonPath("$.errors[0].field").value("id"));
        verifyNoInteractions(offres);
    }

    @Test
    void campaignWithUnknownStatusReturns400() throws Exception {
        mvc.perform(patch("/api/campaigns/7/status")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"status":"UNKNOWN"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("JSON invalide"))
                .andExpect(jsonPath("$.errors[0].field").value("status"));
        verifyNoInteractions(campaigns);
    }

    @Test
    void invalidCampaignPayoutFromServiceUsesProblemDetail() throws Exception {
        when(campaigns.create(eq(7L), any()))
                .thenThrow(new InputValidationException("payoutValue", "Pourcentage entre 0 et 100"));

        mvc.perform(post("/api/offres/7/campaigns").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Campagne","startDate":"2030-01-01","endDate":"2030-01-02",
                                 "budget":100,"payoutModel":"CPA_PERCENT","payoutValue":101}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Pourcentage entre 0 et 100"))
                .andExpect(jsonPath("$.errors[0].field").value("payoutValue"));
    }
}