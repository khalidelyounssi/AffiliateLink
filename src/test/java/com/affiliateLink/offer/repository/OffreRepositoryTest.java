package com.affiliateLink.offer.repository;

import com.affiliateLink.offer.entity.*;
import com.affiliateLink.offer.service.OffreSpecifications;
import com.affiliateLink.offer.service.TagService;
import com.affiliateLink.support.PostgresTestConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.affiliateLink.offer.entity.OfferNiche.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({PostgresTestConfig.class, TagService.class})
class OffreRepositoryIT {
    @Autowired OffreRepository offres;
    @Autowired CampaignRepository campaigns;
    @Autowired TagRepository tags;
    @Autowired TagService tagService;
    @Autowired EntityManager entityManager;

    private Offre clavier;
    private Offre souris;

    @BeforeEach
    void prepareData() {
        clavier = persist(7L, "Clavier", TECH, Set.of(" Java ", "JAVA"));
        souris = persist(9L, "Souris", TECH, Set.of("java"));
        persist(7L, "Sac de voyage", TRAVEL, Set.of("voyage"));
        entityManager.flush();
        entityManager.clear();
    }

    @ParameterizedTest
    @MethodSource("filters")
    void appliesTheRequestedFilters(
            OfferNiche niche, Long advertiserId, String tag, String keyword,
            List<String> expectedTitles) {

        var spec = OffreSpecifications.filter(niche, advertiserId, tag, keyword);
        var page = offres.findAll(spec, PageRequest.of(0, 10, Sort.by("title")));

        assertThat(page.getContent()).extracting(Offre::getTitle)
                .containsExactlyElementsOf(expectedTitles);
        assertThat(page.getTotalElements()).isEqualTo(expectedTitles.size());
    }

    static Stream<Arguments> filters() {
        return Stream.of(
                Arguments.of(TECH, null, null, null, List.of("Clavier", "Souris")),
                Arguments.of(null, 7L, null, null, List.of("Clavier", "Sac de voyage")),
                Arguments.of(null, null, " JAVA ", null, List.of("Clavier", "Souris")),
                Arguments.of(null, null, null, "LAV", List.of("Clavier")),
                Arguments.of(TECH, 7L, "java", "clav", List.of("Clavier")),
                Arguments.of(null, null, null, null, List.of("Clavier", "Sac de voyage", "Souris")));
    }

    @Test
    void sortsBeforeTakingSecondPage() {
        var page = offres.findAll(
                OffreSpecifications.filter(null, null, null, null),
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "title")));

        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).extracting(Offre::getTitle)
                .containsExactly("Sac de voyage");
    }

    @Test
    void persistsOneNormalizedTagSharedByTwoOffers() {
        Tag java = tags.findByName("java").orElseThrow();
        var savedClavier = offres.findById(clavier.getId()).orElseThrow();
        var savedSouris = offres.findById(souris.getId()).orElseThrow();

        assertThat(savedClavier.getTags()).extracting(Tag::getId).containsExactly(java.getId());
        assertThat(savedSouris.getTags()).extracting(Tag::getId).containsExactly(java.getId());
        assertThat(tags.findAll()).extracting(Tag::getName)
                .containsExactlyInAnyOrder("java", "voyage");
    }

    @Test
    void jpqlQueriesReturnOwnerAndLinkedOffer() {
        var persisted = offres.findLockedById(clavier.getId()).orElseThrow();
        var campaign = new Campaign("Promo", LocalDate.of(2030, 1, 1),
                LocalDate.of(2030, 1, 2), BigDecimal.TEN, PayoutModel.CPC, BigDecimal.ONE);
        campaign.attachTo(persisted);
        campaigns.saveAndFlush(campaign);

        assertThat(offres.findAdvertiserId(persisted.getId())).contains(7L);
        assertThat(campaigns.findOffreId(campaign.getId())).contains(persisted.getId());
    }

    private Offre persist(Long owner, String title, OfferNiche niche, Set<String> rawTags) {
        var offre = new Offre(owner, title, "Description", "https://example.test", niche);
        offre.replaceTags(tagService.resolve(rawTags));
        return offres.saveAndFlush(offre);
    }
}