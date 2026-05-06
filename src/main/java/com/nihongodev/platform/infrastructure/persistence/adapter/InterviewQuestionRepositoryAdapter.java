package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.InterviewQuestionRepositoryPort;
import com.nihongodev.platform.domain.model.InterviewDifficulty;
import com.nihongodev.platform.domain.model.InterviewQuestion;
import com.nihongodev.platform.domain.model.InterviewType;
import com.nihongodev.platform.infrastructure.persistence.mapper.InterviewQuestionPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaInterviewQuestionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class InterviewQuestionRepositoryAdapter implements InterviewQuestionRepositoryPort {

    private final JpaInterviewQuestionRepository jpaRepository;
    private final InterviewQuestionPersistenceMapper mapper;

    public InterviewQuestionRepositoryAdapter(JpaInterviewQuestionRepository jpaRepository,
                                              InterviewQuestionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public InterviewQuestion save(InterviewQuestion question) {
        var entity = mapper.toEntity(question);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<InterviewQuestion> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<InterviewQuestion> findByTypeAndDifficulty(InterviewType type, InterviewDifficulty difficulty) {
        return jpaRepository.findByInterviewTypeAndDifficulty(type.name(), difficulty.name())
                .stream().map(mapper::toDomain).toList();
    }
}
