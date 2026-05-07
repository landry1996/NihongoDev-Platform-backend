package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.domain.model.Priority;

public record WeakAreaDto(
        ModuleType moduleType,
        String topic,
        double averageScore,
        Priority priority
) {}
