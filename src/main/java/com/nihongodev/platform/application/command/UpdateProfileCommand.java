package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import java.util.UUID;

public record UpdateProfileCommand(
        UUID userId,
        String firstName,
        String lastName,
        JapaneseLevel japaneseLevel,
        String objective
) {}
