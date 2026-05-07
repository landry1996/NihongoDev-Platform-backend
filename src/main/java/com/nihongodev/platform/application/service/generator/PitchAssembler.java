package com.nihongodev.platform.application.service.generator;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.PitchType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PitchAssembler {

    private final Map<PitchType, List<PitchSection>> registry;

    public PitchAssembler(Map<PitchType, List<PitchSection>> registry) {
        this.registry = registry;
    }

    public String assemble(CvProfile profile, PitchType type) {
        List<PitchSection> sections = registry.get(type);
        if (sections == null || sections.isEmpty()) {
            throw new IllegalArgumentException("No sections registered for pitch type: " + type);
        }
        return sections.stream()
                .sorted(Comparator.comparingInt(PitchSection::order))
                .map(section -> section.render(profile))
                .filter(content -> content != null && !content.isBlank())
                .collect(Collectors.joining("\n\n"));
    }
}
