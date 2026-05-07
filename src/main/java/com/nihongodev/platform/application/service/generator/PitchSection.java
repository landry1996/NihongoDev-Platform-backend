package com.nihongodev.platform.application.service.generator;

import com.nihongodev.platform.domain.model.CvProfile;

public interface PitchSection {
    String render(CvProfile profile);
    int order();
}
