package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.SubmitAnswerCommand;
import com.nihongodev.platform.application.dto.AnswerResultDto;
import com.nihongodev.platform.application.port.in.SubmitAnswerPort;
import com.nihongodev.platform.application.port.out.QuestionRepositoryPort;
import com.nihongodev.platform.application.port.out.QuizAttemptRepositoryPort;
import com.nihongodev.platform.application.service.QuestionCorrectorFactory;
import com.nihongodev.platform.application.service.ScoreCalculator;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Question;
import com.nihongodev.platform.domain.model.QuizAttempt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmitAnswerUseCase implements SubmitAnswerPort {

    private final QuizAttemptRepositoryPort attemptRepository;
    private final QuestionRepositoryPort questionRepository;

    public SubmitAnswerUseCase(QuizAttemptRepositoryPort attemptRepository,
                               QuestionRepositoryPort questionRepository) {
        this.attemptRepository = attemptRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public AnswerResultDto submit(SubmitAnswerCommand command) {
        QuizAttempt attempt = attemptRepository.findById(command.attemptId())
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt", "id", command.attemptId()));

        if (!attempt.isInProgress()) {
            throw new BusinessException("Quiz attempt is no longer in progress");
        }

        Question question = questionRepository.findById(command.questionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", command.questionId()));

        boolean correct = QuestionCorrectorFactory.getCorrector(question.getQuestionType())
                .correct(question, command.userAnswer());

        double pointsEarned = 0;
        if (correct) {
            attempt.recordCorrectAnswer();
            pointsEarned = ScoreCalculator.calculatePoints(
                    question.getPoints(), command.timeSpentSeconds(),
                    attempt.getStreakMultiplier(), question.getDifficultyLevel()
            );
        } else {
            attempt.recordIncorrectAnswer();
        }

        attemptRepository.save(attempt);

        return new AnswerResultDto(
                question.getId(), correct, pointsEarned,
                question.getCorrectAnswer(), question.getExplanation(),
                attempt.getCurrentStreak(), attempt.getStreakMultiplier(),
                attempt.isGameOver()
        );
    }
}
