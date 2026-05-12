package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.ContentDomain;
import com.nihongodev.platform.domain.model.ContentSource;

public record IngestContentCommand(
    String title,
    String body,
    String sourceUrl,
    ContentSource source,
    ContentDomain domain,
    String authorName
) {}
