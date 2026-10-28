package com.affiliateLink.offer.repository;

import com.affiliateLink.offer.entity.Offre;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface OffreRepository
        extends JpaRepository<Offre, Long>, JpaSpecificationExecutor<Offre> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Offre o where o.id = :id")
    Optional<Offre> findLockedById(@Param("id") Long id);

    @Query("select o.advertiserId from Offre o where o.id = :id")
    Optional<Long> findAdvertiserId(@Param("id") Long id);

    List<Offre> findByAdvertiserIdOrderByIdAsc(Long advertiserId);
}
