package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role,
        JapaneseLevel japaneseLevel,
        String objective,
        String avatarUrl,
        boolean active,
        LocalDateTime createdAt
) {}
