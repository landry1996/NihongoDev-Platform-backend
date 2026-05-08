package com.nihongodev.platform.application.service.cultural;

import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.KeigoViolation;
import com.nihongodev.platform.domain.model.RelationshipType;

import java.util.List;

public interface KeigoAnalysisStep {
    List<KeigoViolation> analyze(String text, KeigoLevel expectedLevel, RelationshipType relationship);
}
