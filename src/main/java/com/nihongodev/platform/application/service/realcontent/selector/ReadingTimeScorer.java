package com.nihongodev.platform.application.service.realcontent.selector;

import com.nihongodev.platform.domain.model.RealContent;
import com.nihongodev.platform.domain.model.UserContentPreference;
import org.springframework.stereotype.Component;

@Component
public class ReadingTimeScorer implements ContentScorer {

    @Override
    public String dimension() {
        return "reading_time";
    }

    @Override
    public double score(RealContent content, UserContentPreference preference) {
        int preferred = preference.preferredReadingMinutes();
        int estimated = content.getEstimatedReadingMinutes();

        if (preferred <= 0) return 0.5;

        double ratio = (double) estimated / preferred;
        if (ratio >= 0.8 && ratio <= 1.2) return 1.0;
        if (ratio >= 0.5 && ratio <= 1.5) return 0.7;
        return 0.3;
    }

    @Override
    public double weight() {
        return 0.15;
    }
}
