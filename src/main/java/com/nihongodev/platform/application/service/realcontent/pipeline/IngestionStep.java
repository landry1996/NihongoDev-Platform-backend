package com.nihongodev.platform.application.service.realcontent.pipeline;

import com.nihongodev.platform.domain.model.RealContent;

public interface IngestionStep {
    int order();
    void process(RealContent content);
}
