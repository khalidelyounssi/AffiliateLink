package com.affiliateLink.profile.repository;

import com.affiliateLink.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Profile p where p.id = :id")
    Optional<Profile> findLockedById(@Param("id") Long id);

    boolean existsByEmailIgnoreCase(String email);
}