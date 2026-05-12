package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CodeExerciseAttemptDto;
import com.nihongodev.platform.application.dto.CodeExerciseScoreDto;
import com.nihongodev.platform.application.dto.CommitMessageAnalysisDto;
import com.nihongodev.platform.application.port.in.GetCodeExerciseHistoryPort;
import com.nihongodev.platform.application.port.out.CodeExerciseAttemptRepositoryPort;
import com.nihongodev.platform.domain.model.CodeExerciseAttempt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetCodeExerciseHistoryUseCase implements GetCodeExerciseHistoryPort {

    private final CodeExerciseAttemptRepositoryPort attemptRepository;

    public GetCodeExerciseHistoryUseCase(CodeExerciseAttemptRepositoryPort attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @Override
    public List<CodeExerciseAttemptDto> getByUserId(UUID userId) {
        return attemptRepository.findByUserId(userId).stream()
            .map(this::toDto)
            .toList();
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
