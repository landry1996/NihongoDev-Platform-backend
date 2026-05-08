package com.nihongodev.platform.application.service.cultural;

import com.nihongodev.platform.domain.model.KeigoViolation;

import java.util.List;

public record KeigoValidationResult(int score, List<KeigoViolation> violations) {
    public static KeigoValidationResult perfect() {
        return new KeigoValidationResult(100, List.of());
    }
}
