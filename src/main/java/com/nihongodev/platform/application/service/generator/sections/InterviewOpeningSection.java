package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class InterviewOpeningSection implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Interview Presentation\n\n");
        sb.append("*Structured self-introduction for a technical interview*\n\n");
        sb.append("Thank you for this opportunity. I'd like to take a moment to introduce myself.\n\n");
        sb.append("My name is **").append(profile.getFullName()).append("**");
        sb.append(" and I'm applying for the **").append(profile.getTargetRole()).append("** position.");
        sb.append(" I bring **").append(profile.getYearsOfExperience()).append(" years** of professional experience.");

        return sb.toString();
    }

    @Override
    public int order() { return 5; }
}
