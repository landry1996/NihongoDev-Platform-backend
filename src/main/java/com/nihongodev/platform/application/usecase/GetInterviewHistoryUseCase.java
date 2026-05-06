package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.InterviewSessionDto;
import com.nihongodev.platform.application.port.in.GetInterviewHistoryPort;
import com.nihongodev.platform.application.port.out.InterviewSessionRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.InterviewSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetInterviewHistoryUseCase implements GetInterviewHistoryPort {

    private final InterviewSessionRepositoryPort sessionRepository;

    public GetInterviewHistoryUseCase(InterviewSessionRepositoryPort sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<InterviewSessionDto> getUserHistory(UUID userId) {
        return sessionRepository.findByUserId(userId).stream().map(this::mapToDto).toList();
    }

    @Override
    public InterviewSessionDto getSessionById(UUID userId, UUID sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewSession", "id", sessionId));

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized: this session belongs to another user");
        }

        return mapToDto(session);
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
