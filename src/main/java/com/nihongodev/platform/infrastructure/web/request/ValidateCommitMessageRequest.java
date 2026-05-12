package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.CommitPrefix;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ValidateCommitMessageRequest(
    @NotBlank @Size(max = 200) String commitMessage,
    @NotNull CommitPrefix expectedPrefix,
    boolean requireTaigenDome,
    boolean requireScope,
    String expectedScope
) {}
