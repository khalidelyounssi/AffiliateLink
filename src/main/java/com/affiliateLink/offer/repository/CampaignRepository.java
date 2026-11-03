package com.affiliateLink.offer.repository;

import com.affiliateLink.offer.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByOffreIdAndStatusOrderByIdAsc(Long offreId, CampaignStatus status);

    List<Campaign> findByStatusOrderByIdAsc(CampaignStatus status);

    @Query("select c.offre.id from Campaign c where c.id = :id")
    Optional<Long> findOffreId(@Param("id") Long id);
}
