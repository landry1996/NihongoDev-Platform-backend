package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.WorkExperience;

public class ExperienceSectionJP implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getExperiences() == null || profile.getExperiences().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## 職歴\n");

        for (WorkExperience exp : profile.getExperiences()) {
            int years = exp.getDurationMonths() / 12;
            int months = exp.getDurationMonths() % 12;
            String duration = years > 0
                    ? years + "年" + (months > 0 ? months + "ヶ月" : "")
                    : months + "ヶ月";

            sb.append("\n### ").append(exp.getCompany()).append(" — ").append(exp.getRole());
            sb.append("\n*").append(duration).append("*\n");

            if (exp.getHighlights() != null) {
                for (String highlight : exp.getHighlights()) {
                    sb.append("\n- ").append(highlight);
                }
            }
            sb.append("\n");
        }

        return sb.toString().stripTrailing();
    }

    @Override
    public int order() { return 20; }
}
