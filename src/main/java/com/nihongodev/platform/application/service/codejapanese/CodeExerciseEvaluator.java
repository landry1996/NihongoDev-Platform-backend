package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.domain.model.CodeExerciseScore;
import com.nihongodev.platform.domain.model.CodeReviewExercise;
import com.nihongodev.platform.domain.model.CommitMessageAnalysis;
import com.nihongodev.platform.domain.model.TechnicalJapaneseViolation;

import java.util.List;

public interface CodeExerciseEvaluator {
    CodeExerciseEvaluationResult evaluate(CodeReviewExercise exercise, String userResponse);

    record CodeExerciseEvaluationResult(
        CodeExerciseScore score,
        List<TechnicalJapaneseViolation> violations,
        CommitMessageAnalysis commitAnalysis,
        String feedback
    ) {}
}
