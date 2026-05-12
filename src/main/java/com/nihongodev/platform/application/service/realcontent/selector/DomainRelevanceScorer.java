package com.nihongodev.platform.application.service.realcontent.selector;

import com.nihongodev.platform.domain.model.RealContent;
import com.nihongodev.platform.domain.model.UserContentPreference;
import org.springframework.stereotype.Component;

@Component
public class DomainRelevanceScorer implements ContentScorer {

    @Override
    public String dimension() {
        return "domain_relevance";
    }

    @Override
    public double score(RealContent content, UserContentPreference preference) {
        if (preference.preferredDomains().contains(content.getDomain())) {
            return 1.0;
        }
        return 0.3;
    }

    @Override
    public double weight() {
        return 0.35;
    }
}
