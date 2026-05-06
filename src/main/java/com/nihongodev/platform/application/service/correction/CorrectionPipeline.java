package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.Annotation;
import com.nihongodev.platform.domain.model.CorrectionContext;
import com.nihongodev.platform.domain.model.CorrectionRule;
import com.nihongodev.platform.domain.model.CorrectionScore;

import java.util.ArrayList;
import java.util.List;

public class CorrectionPipeline {

    private final List<CorrectionStep> steps;
    private final CorrectionEngine engine;
    private final CorrectionMerger merger;

    public CorrectionPipeline(List<CorrectionStep> steps, CorrectionEngine engine) {
        this.steps = steps;
        this.engine = engine;
        this.merger = new CorrectionMerger();
    }

    public CorrectionPipelineResult execute(String text, CorrectionContext context,
                                            List<CorrectionRule> rules) {
        List<CorrectionScore> scores = new ArrayList<>();
        List<Annotation> allAnnotations = new ArrayList<>();

        for (CorrectionStep step : steps) {
            CorrectionStep.CorrectionStepResult result = step.process(text, context);
            scores.add(result.score());
            allAnnotations.addAll(result.annotations());
        }

        List<Annotation> ruleAnnotations = engine.applyRules(text, rules, context);
        allAnnotations.addAll(ruleAnnotations);

        CorrectionScore finalScore = merger.mergeScores(scores);
        List<Annotation> mergedAnnotations = merger.mergeAnnotations(allAnnotations);

        return new CorrectionPipelineResult(finalScore, mergedAnnotations);
    }

    public record CorrectionPipelineResult(
            CorrectionScore score,
            List<Annotation> annotations
    ) {}
}
