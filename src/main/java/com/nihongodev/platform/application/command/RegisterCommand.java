package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.Role;

public record RegisterCommand(
        String firstName,
        String lastName,
        String email,
        String password,
        Role role,
        JapaneseLevel japaneseLevel,
        String objective
) {}
