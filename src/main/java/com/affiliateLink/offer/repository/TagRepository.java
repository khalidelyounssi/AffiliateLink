package com.affiliateLink.offer.repository;

import com.affiliateLink.offer.entity.Tag;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Modifying
    @Query(
            value = "INSERT INTO offer_tags(name) VALUES (:name) ON CONFLICT (name) DO NOTHING",
            nativeQuery = true)
    void insertIfMissing(@Param("name") String name);
}
