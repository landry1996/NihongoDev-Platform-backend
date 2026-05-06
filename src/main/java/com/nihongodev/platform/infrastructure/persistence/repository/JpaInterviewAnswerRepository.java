package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.InterviewAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaInterviewAnswerRepository extends JpaRepository<InterviewAnswerEntity, UUID> {
    List<InterviewAnswerEntity> findBySessionId(UUID sessionId);
}
