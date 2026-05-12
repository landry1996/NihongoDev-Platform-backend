package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.application.service.codejapanese.evaluators.*;
import com.nihongodev.platform.domain.model.ExerciseType;
import org.springframework.stereotype.Component;

@Component
public class CodeExerciseEvaluatorFactory {

    private final CodeReviewEvaluator codeReviewEvaluator;
    private final PRWritingEvaluator prWritingEvaluator;
    private final CommitMessageEvaluator commitMessageEvaluator;
    private final BugReportEvaluator bugReportEvaluator;
    private final TechDiscussionEvaluator techDiscussionEvaluator;

    public CodeExerciseEvaluatorFactory(CodeReviewEvaluator codeReviewEvaluator,
                                        PRWritingEvaluator prWritingEvaluator,
                                        CommitMessageEvaluator commitMessageEvaluator,
                                        BugReportEvaluator bugReportEvaluator,
                                        TechDiscussionEvaluator techDiscussionEvaluator) {
        this.codeReviewEvaluator = codeReviewEvaluator;
        this.prWritingEvaluator = prWritingEvaluator;
        this.commitMessageEvaluator = commitMessageEvaluator;
        this.bugReportEvaluator = bugReportEvaluator;
        this.techDiscussionEvaluator = techDiscussionEvaluator;
    }

    public CodeExerciseEvaluator getEvaluator(ExerciseType type) {
        return switch (type) {
            case CODE_REVIEW -> codeReviewEvaluator;
            case PR_WRITING -> prWritingEvaluator;
            case COMMIT_MESSAGE -> commitMessageEvaluator;
            case BUG_REPORT -> bugReportEvaluator;
            case TECH_DISCUSSION -> techDiscussionEvaluator;
        };
    }
}
