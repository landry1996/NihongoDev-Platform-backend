package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.ContentDomain;
import com.nihongodev.platform.domain.model.ContentSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IngestContentRequest(
    @NotBlank @Size(max = 300) String title,
    @NotBlank String body,
    @Size(max = 1000) String sourceUrl,
    @NotNull ContentSource source,
    @NotNull ContentDomain domain,
    @Size(max = 200) String authorName
) {}
