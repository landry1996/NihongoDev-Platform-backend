package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.QuizResultDto;
import com.nihongodev.platform.application.port.in.GetQuizHistoryPort;
import com.nihongodev.platform.application.port.out.QuizResultRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.QuizResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetQuizHistoryUseCase implements GetQuizHistoryPort {

    private final QuizResultRepositoryPort resultRepository;

    public GetQuizHistoryUseCase(QuizResultRepositoryPort resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public List<QuizResultDto> getUserHistory(UUID userId) {
        return resultRepository.findByUserId(userId).stream().map(this::mapToDto).toList();
    }

    @Override
    public QuizResultDto getResultByAttempt(UUID attemptId) {
        QuizResult result = resultRepository.findByAttemptId(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizResult", "attemptId", attemptId));
        return mapToDto(result);
    }

    private QuizResultDto mapToDto(QuizResult r) {
        return new QuizResultDto(
                r.getId(), r.getQuizId(), r.getTotalQuestions(), r.getCorrectAnswers(),
                r.getTotalScore(), r.getMaxPossibleScore(), r.getPercentage(),
                r.isPassed(), r.getMaxStreak(), r.getAverageTimePerQuestion(),
                r.getDifficultyReached(), r.getCompletedAt()
        );
    }
}
