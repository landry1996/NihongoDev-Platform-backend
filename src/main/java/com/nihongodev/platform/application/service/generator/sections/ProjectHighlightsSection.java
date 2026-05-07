package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class ProjectHighlightsSection implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getNotableProjects() == null || profile.getNotableProjects().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Notable Projects\n");
        for (String project : profile.getNotableProjects()) {
            sb.append("\n- ").append(project);
        }

        return sb.toString();
    }

    @Override
    public int order() { return 45; }
}
