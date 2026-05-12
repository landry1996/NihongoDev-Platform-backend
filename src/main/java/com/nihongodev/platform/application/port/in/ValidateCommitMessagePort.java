package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.ValidateCommitMessageCommand;
import com.nihongodev.platform.application.dto.CommitMessageAnalysisDto;

public interface ValidateCommitMessagePort {
    CommitMessageAnalysisDto execute(ValidateCommitMessageCommand command);
}
