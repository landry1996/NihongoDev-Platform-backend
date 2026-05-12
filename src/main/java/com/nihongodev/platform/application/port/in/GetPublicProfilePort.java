package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.PublicProfileDto;

import java.util.UUID;

public interface GetPublicProfilePort {
    PublicProfileDto getBySlug(String slug, boolean isRecruiter);
    PublicProfileDto getByUserId(UUID userId);
}
