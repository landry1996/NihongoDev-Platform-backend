package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.CorrectionSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCorrectionSessionRepository extends JpaRepository<CorrectionSessionEntity, UUID> {
    List<CorrectionSessionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    int countByUserId(UUID userId);
}
