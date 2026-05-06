package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.InterviewSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaInterviewSessionRepository extends JpaRepository<InterviewSessionEntity, UUID> {
    List<InterviewSessionEntity> findByUserId(UUID userId);
}
