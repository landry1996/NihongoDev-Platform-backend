package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.QuizResultDto;
import com.nihongodev.platform.application.port.in.CompleteQuizPort;
import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.event.QuizCompletedEvent;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class CompleteQuizUseCase implements CompleteQuizPort {

    private final QuizAttemptRepositoryPort attemptRepository;
    private final QuizRepositoryPort quizRepository;
    private final QuestionRepositoryPort questionRepository;
    private final QuizResultRepositoryPort resultRepository;
    private final EventPublisherPort eventPublisher;

    public CompleteQuizUseCase(QuizAttemptRepositoryPort attemptRepository,
                               QuizRepositoryPort quizRepository,
                               QuestionRepositoryPort questionRepository,
                               QuizResultRepositoryPort resultRepository,
                               EventPublisherPort eventPublisher) {
        this.attemptRepository = attemptRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public QuizResultDto complete(UUID userId, UUID attemptId) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt", "id", attemptId));

        if (!attempt.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized: this attempt belongs to another user");
        }

        if (!attempt.isInProgress() && !attempt.isGameOver()) {
            throw new BusinessException("Quiz attempt is already completed");
        }

        Quiz quiz = quizRepository.findById(attempt.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", attempt.getQuizId()));

        List<Question> questions = questionRepository.findByQuizId(quiz.getId());
        int totalQuestions = questions.size();
        double maxPossibleScore = questions.stream().mapToInt(Question::getPoints).sum();

        int timeSpent = (int) Duration.between(attempt.getStartedAt(), java.time.LocalDateTime.now()).getSeconds();
        attempt.complete(timeSpent);
        attemptRepository.save(attempt);

        int correctAnswers = attempt.getMaxStreak();
        double totalScore = correctAnswers * 1.0;
        double avgTime = totalQuestions > 0 ? (double) timeSpent / totalQuestions : 0;

        DifficultyLevel difficultyReached = DifficultyLevel.MEDIUM;
        if (attempt.getMaxStreak() >= 8) difficultyReached = DifficultyLevel.HARD;
        else if (attempt.getMaxStreak() < 3) difficultyReached = DifficultyLevel.EASY;

        QuizResult result = QuizResult.calculate(
                attemptId, quiz.getId(), userId, totalQuestions, correctAnswers,
                totalScore, maxPossibleScore, quiz.getPassingScore(),
                attempt.getMaxStreak(), avgTime, difficultyReached
        );

        QuizResult saved = resultRepository.save(result);

        eventPublisher.publish("quiz-events", QuizCompletedEvent.of(
                userId, quiz.getId(), attemptId, quiz.getTitle(),
                saved.getPercentage(), saved.isPassed(),
                attempt.getMaxStreak(), attempt.getMode().name()
        ));

        return mapToDto(saved);
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
