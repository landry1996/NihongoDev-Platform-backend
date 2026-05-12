package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CodeExerciseAttemptDto;
import com.nihongodev.platform.application.dto.CodeExerciseScoreDto;
import com.nihongodev.platform.application.dto.CommitMessageAnalysisDto;
import com.nihongodev.platform.application.command.SubmitCodeExerciseResponseCommand;
import com.nihongodev.platform.application.port.in.SubmitCodeExerciseResponsePort;
import com.nihongodev.platform.application.port.out.CodeExerciseAttemptRepositoryPort;
import com.nihongodev.platform.application.port.out.CodeExerciseRepositoryPort;
import com.nihongodev.platform.application.port.out.CodeJapaneseProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.service.codejapanese.CodeExerciseEvaluator;
import com.nihongodev.platform.application.service.codejapanese.CodeExerciseEvaluatorFactory;
import com.nihongodev.platform.domain.event.CodeExerciseCompletedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmitCodeExerciseResponseUseCase implements SubmitCodeExerciseResponsePort {

    private final CodeExerciseRepositoryPort exerciseRepository;
    private final CodeExerciseAttemptRepositoryPort attemptRepository;
    private final CodeJapaneseProgressRepositoryPort progressRepository;
    private final CodeExerciseEvaluatorFactory evaluatorFactory;
    private final EventPublisherPort eventPublisher;

    public SubmitCodeExerciseResponseUseCase(CodeExerciseRepositoryPort exerciseRepository,
                                             CodeExerciseAttemptRepositoryPort attemptRepository,
                                             CodeJapaneseProgressRepositoryPort progressRepository,
                                             CodeExerciseEvaluatorFactory evaluatorFactory,
                                             EventPublisherPort eventPublisher) {
        this.exerciseRepository = exerciseRepository;
        this.attemptRepository = attemptRepository;
        this.progressRepository = progressRepository;
        this.evaluatorFactory = evaluatorFactory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public CodeExerciseAttemptDto execute(SubmitCodeExerciseResponseCommand command) {
        CodeReviewExercise exercise = exerciseRepository.findById(command.exerciseId())
            .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + command.exerciseId()));

        CodeExerciseEvaluator evaluator = evaluatorFactory.getEvaluator(exercise.getType());
        CodeExerciseEvaluator.CodeExerciseEvaluationResult result = evaluator.evaluate(exercise, command.response());

        CodeExerciseAttempt attempt = CodeExerciseAttempt.create(
            command.userId(), command.exerciseId(), exercise.getType(),
            command.response(), command.timeSpentSeconds()
        );
        attempt.setScore(result.score());
        attempt.setViolations(result.violations());
        attempt.setCommitAnalysis(result.commitAnalysis());
        attempt.setFeedback(result.feedback());

        attemptRepository.save(attempt);

        CodeJapaneseProgress progress = progressRepository
            .findByUserIdAndExerciseType(command.userId(), exercise.getType())
            .orElse(CodeJapaneseProgress.create(command.userId(), exercise.getType()));
        progress.recordAttempt(result.score().overallScore());
        progressRepository.save(progress);

        eventPublisher.publish("code-japanese-events", CodeExerciseCompletedEvent.create(
            command.userId(), command.exerciseId(), exercise.getType(), result.score().overallScore()
        ));

        return toDto(attempt);
    }

    private CodeExerciseAttemptDto toDto(CodeExerciseAttempt a) {
        CodeExerciseScoreDto scoreDto = a.getScore() != null ? new CodeExerciseScoreDto(
            a.getScore().technicalAccuracyScore(), a.getScore().japaneseQualityScore(),
            a.getScore().professionalToneScore(), a.getScore().structureScore(),
            a.getScore().teamCommunicationScore(), a.getScore().overallScore()
        ) : null;

        CommitMessageAnalysisDto analysisDto = a.getCommitAnalysis() != null ? new CommitMessageAnalysisDto(
            a.getCommitAnalysis().hasValidPrefix(), a.getCommitAnalysis().detectedPrefix(),
            a.getCommitAnalysis().isTaigenDome(), a.getCommitAnalysis().hasScope(),
            a.getCommitAnalysis().detectedScope(), a.getCommitAnalysis().length(),
            a.getCommitAnalysis().isWithinMaxLength(), a.getCommitAnalysis().detectedVerbEndings(),
            a.getCommitAnalysis().commitScore()
        ) : null;

        return new CodeExerciseAttemptDto(
            a.getId(), a.getExerciseId(), a.getExerciseType(), a.getUserResponse(),
            scoreDto, a.getViolations(), analysisDto, a.getFeedback(),
            a.getTimeSpentSeconds(), a.getAttemptedAt()
        );
    }
}
