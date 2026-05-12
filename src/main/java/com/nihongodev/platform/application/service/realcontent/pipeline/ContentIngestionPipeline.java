package com.nihongodev.platform.application.service.realcontent.pipeline;

import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ContentIngestionPipeline {

    private final List<IngestionStep> steps;

    public ContentIngestionPipeline(List<IngestionStep> steps) {
        this.steps = steps.stream()
            .sorted(Comparator.comparingInt(IngestionStep::order))
            .toList();
    }

    public void process(RealContent content) {
        for (IngestionStep step : steps) {
            step.process(content);
        }
    }
}
