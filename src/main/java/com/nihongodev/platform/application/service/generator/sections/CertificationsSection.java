package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class CertificationsSection implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getCertifications() == null || profile.getCertifications().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Certifications\n");
        for (String cert : profile.getCertifications()) {
            sb.append("\n- ").append(cert);
        }

        return sb.toString();
    }

    @Override
    public int order() { return 50; }
}
