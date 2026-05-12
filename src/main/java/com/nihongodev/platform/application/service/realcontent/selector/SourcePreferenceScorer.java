package com.nihongodev.platform.application.service.realcontent.selector;

import com.nihongodev.platform.domain.model.RealContent;
import com.nihongodev.platform.domain.model.UserContentPreference;
import org.springframework.stereotype.Component;

@Component
public class SourcePreferenceScorer implements ContentScorer {

    @Override
    public String dimension() {
        return "source_preference";
    }

    @Override
    public double score(RealContent content, UserContentPreference preference) {
        if (preference.preferredSources().contains(content.getSource())) {
            return 1.0;
        }
        return 0.4;
    }

    @Override
    public double weight() {
        return 0.20;
    }
}
