package com.nihongodev.platform.application.service.realcontent.selector;

import com.nihongodev.platform.domain.model.RealContent;
import com.nihongodev.platform.domain.model.UserContentPreference;

public interface ContentScorer {
    String dimension();
    double score(RealContent content, UserContentPreference preference);
    double weight();
}
