package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.InterviewQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaInterviewQuestionRepository extends JpaRepository<InterviewQuestionEntity, UUID> {
    List<InterviewQuestionEntity> findByInterviewTypeAndDifficulty(String interviewType, String difficulty);
}
