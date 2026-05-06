package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.Annotation;
import com.nihongodev.platform.domain.model.CorrectionContext;
import com.nihongodev.platform.domain.model.CorrectionRule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternCorrectionEngine implements CorrectionEngine {

    @Override
    public List<Annotation> applyRules(String text, List<CorrectionRule> rules, CorrectionContext context) {
        List<Annotation> annotations = new ArrayList<>();

        for (CorrectionRule rule : rules) {
            if (!rule.appliesTo(context)) continue;

            try {
                Pattern pattern = Pattern.compile(rule.getPattern());
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    annotations.add(Annotation.create(
                            matcher.start(), matcher.end(),
                            rule.getSeverity(), rule.getCategory(),
                            matcher.group(), rule.getSuggestionTemplate(),
                            rule.getExplanationTemplate(),
                            rule.getId().toString()
                    ));
                }
            } catch (Exception e) {
                // Skip invalid regex patterns gracefully
            }
        }

        return annotations;
    }
}
