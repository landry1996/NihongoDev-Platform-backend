package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.InterviewQuestionDto;
import com.nihongodev.platform.application.port.in.GetNextQuestionPort;
import com.nihongodev.platform.application.port.out.InterviewQuestionRepositoryPort;
import com.nihongodev.platform.application.port.out.InterviewSessionRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.InterviewQuestion;
import com.nihongodev.platform.domain.model.InterviewSession;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetNextQuestionUseCase implements GetNextQuestionPort {

    private final InterviewSessionRepositoryPort sessionRepository;
    private final InterviewQuestionRepositoryPort questionRepository;

    public GetNextQuestionUseCase(InterviewSessionRepositoryPort sessionRepository,
                                  InterviewQuestionRepositoryPort questionRepository) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public InterviewQuestionDto getNextQuestion(UUID userId, UUID sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewSession", "id", sessionId));

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized: this session belongs to another user");
        }

        if (!session.isInProgress()) {
            throw new BusinessException("Interview session is no longer in progress");
        }

        if (!session.hasMoreQuestions()) {
            throw new BusinessException("No more questions available in this session");
        }

        UUID questionId = session.getQuestionIds().get(session.getCurrentQuestionIndex());
        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewQuestion", "id", questionId));

        return new InterviewQuestionDto(
                question.getId(), question.getInterviewType(), question.getDifficulty(),
                question.getPhase(), question.getContent(), question.getContentJapanese(),
                question.getTimeLimitSeconds(), question.getOrderIndex()
        );
    }
}
