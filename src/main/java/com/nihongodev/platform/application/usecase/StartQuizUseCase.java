package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.QuizAttemptDto;
import com.nihongodev.platform.application.port.in.StartQuizPort;
import com.nihongodev.platform.application.port.out.QuizAttemptRepositoryPort;
import com.nihongodev.platform.application.port.out.QuizRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Quiz;
import com.nihongodev.platform.domain.model.QuizAttempt;
import com.nihongodev.platform.domain.model.QuizMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StartQuizUseCase implements StartQuizPort {

    private final QuizRepositoryPort quizRepository;
    private final QuizAttemptRepositoryPort attemptRepository;

    public StartQuizUseCase(QuizRepositoryPort quizRepository, QuizAttemptRepositoryPort attemptRepository) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
    }

    @Override
    @Transactional
    public QuizAttemptDto start(UUID userId, UUID quizId, QuizMode mode) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        if (quiz.getMaxAttempts() > 0) {
            long attemptCount = attemptRepository.findByUserIdAndQuizId(userId, quizId).size();
            if (attemptCount >= quiz.getMaxAttempts()) {
                throw new BusinessException("Maximum attempts reached for this quiz");
            }
        }

        QuizMode effectiveMode = mode != null ? mode : quiz.getMode();
        QuizAttempt attempt = QuizAttempt.start(quizId, userId, effectiveMode);
        QuizAttempt saved = attemptRepository.save(attempt);

        return new QuizAttemptDto(
                saved.getId(), saved.getQuizId(), saved.getMode(), saved.getStatus(),
                saved.getCurrentStreak(), saved.getMaxStreak(), saved.getLivesRemaining(),
                saved.getStartedAt(), saved.getCompletedAt()
        );
    }
}
