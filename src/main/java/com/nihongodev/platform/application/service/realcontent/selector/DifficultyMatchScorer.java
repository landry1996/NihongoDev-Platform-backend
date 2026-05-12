package com.nihongodev.platform.application.service.realcontent.selector;

import com.nihongodev.platform.domain.model.ReadingDifficulty;
import com.nihongodev.platform.domain.model.RealContent;
import com.nihongodev.platform.domain.model.UserContentPreference;
import org.springframework.stereotype.Component;

@Component
public class DifficultyMatchScorer implements ContentScorer {

    @Override
    public String dimension() {
        return "difficulty_match";
    }

    @Override
    public double score(RealContent content, UserContentPreference preference) {
        ReadingDifficulty contentDiff = content.getReadingDifficulty();
        ReadingDifficulty maxDiff = preference.maxDifficulty();

        if (contentDiff == null || maxDiff == null) return 0.5;

        int contentOrdinal = contentDiff.ordinal();
        int maxOrdinal = maxDiff.ordinal();

        if (contentOrdinal > maxOrdinal) return 0.1;
        if (contentOrdinal == maxOrdinal) return 1.0;
        if (contentOrdinal == maxOrdinal - 1) return 0.8;
        return 0.5;
    }

    @Override
    public double weight() {
        return 0.30;
    }
}
