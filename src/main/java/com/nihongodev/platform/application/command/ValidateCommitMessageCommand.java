package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.CommitPrefix;

public record ValidateCommitMessageCommand(
    String commitMessage,
    CommitPrefix expectedPrefix,
    boolean requireTaigenDome,
    boolean requireScope,
    String expectedScope
) {}
