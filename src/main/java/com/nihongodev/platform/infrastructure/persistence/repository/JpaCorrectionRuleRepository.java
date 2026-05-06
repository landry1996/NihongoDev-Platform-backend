package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.CorrectionRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCorrectionRuleRepository extends JpaRepository<CorrectionRuleEntity, UUID> {
    List<CorrectionRuleEntity> findByActiveTrue();
}
