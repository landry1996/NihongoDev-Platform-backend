package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.InterviewAnswerRepositoryPort;
import com.nihongodev.platform.domain.model.InterviewAnswer;
import com.nihongodev.platform.infrastructure.persistence.mapper.InterviewAnswerPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaInterviewAnswerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class InterviewAnswerRepositoryAdapter implements InterviewAnswerRepositoryPort {

    private final JpaInterviewAnswerRepository jpaRepository;
    private final InterviewAnswerPersistenceMapper mapper;

    public InterviewAnswerRepositoryAdapter(JpaInterviewAnswerRepository jpaRepository,
                                            InterviewAnswerPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public InterviewAnswer save(InterviewAnswer answer) {
        var entity = mapper.toEntity(answer);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<InterviewAnswer> findBySessionId(UUID sessionId) {
        return jpaRepository.findBySessionId(sessionId).stream().map(mapper::toDomain).toList();
    }
}
