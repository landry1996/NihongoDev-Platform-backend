package com.nihongodev.platform.application.service.realcontent.selector;

import com.nihongodev.platform.domain.model.RealContent;
import com.nihongodev.platform.domain.model.UserContentPreference;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class PersonalizedContentSelector {

    private final List<ContentScorer> scorers;

    public PersonalizedContentSelector(List<ContentScorer> scorers) {
        this.scorers = scorers;
    }

    public List<RealContent> selectForUser(List<RealContent> candidates, UserContentPreference preference, int limit) {
        return candidates.stream()
            .map(content -> new ScoredContent(content, computeScore(content, preference)))
            .sorted(Comparator.comparingDouble(ScoredContent::score).reversed())
            .limit(limit)
            .map(ScoredContent::content)
            .toList();
    }

    public double computeScore(RealContent content, UserContentPreference preference) {
        double totalWeight = scorers.stream().mapToDouble(ContentScorer::weight).sum();
        double weightedScore = scorers.stream()
            .mapToDouble(scorer -> scorer.score(content, preference) * scorer.weight())
            .sum();
        return totalWeight > 0 ? weightedScore / totalWeight : 0;
    }

    private record ScoredContent(RealContent content, double score) {}
}
