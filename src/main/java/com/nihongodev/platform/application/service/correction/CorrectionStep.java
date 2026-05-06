package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.Annotation;
import com.nihongodev.platform.domain.model.CorrectionContext;
import com.nihongodev.platform.domain.model.CorrectionScore;

import java.util.List;

public interface CorrectionStep {

    CorrectionStepResult process(String text, CorrectionContext context);

    record CorrectionStepResult(
            CorrectionScore score,
            List<Annotation> annotations
    ) {}
}
