package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class TechStackSection implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getTechStack() == null || profile.getTechStack().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Technical Skills\n\n");
        sb.append(String.join(" | ", profile.getTechStack()));

        return sb.toString();
    }

    @Override
    public int order() { return 30; }
}
