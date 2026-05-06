package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.InterviewSessionDto;
import com.nihongodev.platform.application.port.in.CompleteInterviewPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.InterviewSessionRepositoryPort;
import com.nihongodev.platform.domain.event.InterviewCompletedEvent;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.InterviewSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CompleteInterviewUseCase implements CompleteInterviewPort {

    private final InterviewSessionRepositoryPort sessionRepository;
    private final EventPublisherPort eventPublisher;

    public CompleteInterviewUseCase(InterviewSessionRepositoryPort sessionRepository,
                                    EventPublisherPort eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public InterviewSessionDto complete(UUID userId, UUID sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewSession", "id", sessionId));

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized: this session belongs to another user");
        }

        if (!session.isInProgress()) {
            throw new BusinessException("Interview session is already completed");
        }

        session.complete();
        InterviewSession saved = sessionRepository.save(session);

        eventPublisher.publish("interview-events", InterviewCompletedEvent.of(
                userId, saved.getId(), saved.getInterviewType().name(),
                saved.getOverallScore(), saved.getTotalTimeSpentSeconds(), saved.isPassed()
        ));

        return mapToDto(saved);
    }

    private InterviewSessionDto mapToDto(InterviewSession s) {
        return new InterviewSessionDto(
                s.getId(), s.getInterviewType(), s.getDifficulty(), s.getCurrentPhase(),
                s.getStatus(), s.getCurrentQuestionIndex(), s.getTotalQuestions(),
                s.getOverallScore(), s.getLanguageScore(), s.getTechnicalScore(),
                s.getCommunicationScore(), s.getCulturalScore(), s.isPassed(),
                s.getStartedAt(), s.getCompletedAt()
        );
    }
}
