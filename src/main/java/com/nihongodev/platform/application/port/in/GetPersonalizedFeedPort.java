package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.ContentFeedDto;

import java.util.UUID;

public interface GetPersonalizedFeedPort {
    ContentFeedDto execute(UUID userId);
}
