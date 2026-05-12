package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.RealContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaRealContentRepository extends JpaRepository<RealContentEntity, UUID> {

    List<RealContentEntity> findByStatus(String status);

    @Query("SELECT e FROM RealContentEntity e WHERE e.status = 'PUBLISHED' " +
           "AND e.domain = :domain AND e.readingDifficulty <= :maxDifficulty")
    List<RealContentEntity> findPublishedByDomainAndDifficulty(@Param("domain") String domain,
                                                               @Param("maxDifficulty") String maxDifficulty);

    @Query("SELECT e FROM RealContentEntity e WHERE e.status = 'PUBLISHED'")
    List<RealContentEntity> findPublished();

    @Query("SELECT e FROM RealContentEntity e WHERE e.status = 'PUBLISHED' ORDER BY e.ingestedAt DESC LIMIT :limit")
    List<RealContentEntity> findRecentPublished(@Param("limit") int limit);

    boolean existsBySourceUrl(String sourceUrl);
}
