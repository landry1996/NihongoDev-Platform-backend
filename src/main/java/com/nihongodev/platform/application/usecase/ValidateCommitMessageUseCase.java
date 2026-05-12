package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.ValidateCommitMessageCommand;
import com.nihongodev.platform.application.dto.CommitMessageAnalysisDto;
import com.nihongodev.platform.application.port.in.ValidateCommitMessagePort;
import com.nihongodev.platform.application.service.codejapanese.CommitMessageValidator;
import com.nihongodev.platform.domain.model.CommitMessageAnalysis;
import com.nihongodev.platform.domain.model.CommitMessageRule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidateCommitMessageUseCase implements ValidateCommitMessagePort {

    private final CommitMessageValidator commitValidator;

    public ValidateCommitMessageUseCase(CommitMessageValidator commitValidator) {
        this.commitValidator = commitValidator;
    }

    @Override
    public CommitMessageAnalysisDto execute(ValidateCommitMessageCommand command) {
        CommitMessageRule rule = new CommitMessageRule(
            command.expectedPrefix(),
            command.requireTaigenDome(),
            200,
            command.requireScope(),
            command.expectedScope(),
            List.of("しました", "します", "した"),
            List.of(),
            List.of()
        );

        CommitMessageAnalysis analysis = commitValidator.validate(command.commitMessage(), rule);

        return new CommitMessageAnalysisDto(
            analysis.hasValidPrefix(), analysis.detectedPrefix(),
            analysis.isTaigenDome(), analysis.hasScope(),
            analysis.detectedScope(), analysis.length(),
            analysis.isWithinMaxLength(), analysis.detectedVerbEndings(),
            analysis.commitScore()
        );
    }
}
