package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.Annotation;
import com.nihongodev.platform.domain.model.CorrectionContext;
import com.nihongodev.platform.domain.model.CorrectionRule;

import java.util.List;

public interface CorrectionEngine {

    List<Annotation> applyRules(String text, List<CorrectionRule> rules, CorrectionContext context);
}
