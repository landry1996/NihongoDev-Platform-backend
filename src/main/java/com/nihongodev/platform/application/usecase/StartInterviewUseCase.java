package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.StartInterviewCommand;
import com.nihongodev.platform.application.dto.InterviewSessionDto;
import com.nihongodev.platform.application.port.in.StartInterviewPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.InterviewQuestionRepositoryPort;
import com.nihongodev.platform.application.port.out.InterviewSessionRepositoryPort;
import com.nihongodev.platform.domain.event.InterviewStartedEvent;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.model.InterviewQuestion;
import com.nihongodev.platform.domain.model.InterviewSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class StartInterviewUseCase implements StartInterviewPort {

    private static final int DEFAULT_QUESTION_COUNT = 8;

    private final InterviewSessionRepositoryPort sessionRepository;
    private final InterviewQuestionRepositoryPort questionRepository;
    private final EventPublisherPort eventPublisher;

    public StartInterviewUseCase(InterviewSessionRepositoryPort sessionRepository,
                                 InterviewQuestionRepositoryPort questionRepository,
                                 EventPublisherPort eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public InterviewSessionDto start(UUID userId, StartInterviewCommand command) {
        List<InterviewQuestion> availableQuestions = new java.util.ArrayList<>(questionRepository
                .findByTypeAndDifficulty(command.interviewType(), command.difficulty()));

        if (availableQuestions.isEmpty()) {
            throw new BusinessException("No questions available for type " + command.interviewType()
                    + " and difficulty " + command.difficulty());
        }

        Collections.shuffle(availableQuestions);
        int questionCount = Math.min(DEFAULT_QUESTION_COUNT, availableQuestions.size());
        List<InterviewQuestion> selectedQuestions = availableQuestions.subList(0, questionCount);

        InterviewSession session = InterviewSession.start(userId, command.interviewType(),
                command.difficulty(), questionCount);

        List<UUID> questionIds = selectedQuestions.stream().map(InterviewQuestion::getId).toList();
        session.setQuestionIds(questionIds);

        InterviewSession saved = sessionRepository.save(session);

        eventPublisher.publish("interview-events", InterviewStartedEvent.of(
                userId, saved.getId(), command.interviewType().name(),
                command.difficulty().name(), saved.getStartedAt()
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
