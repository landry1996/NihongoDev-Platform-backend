package com.nihongodev.platform.domain.model;

import java.util.List;
import java.util.Map;

public record PRTemplate(
    List<PRSection> requiredSections,
    Map<PRSection, String> sectionHints,
    Map<PRSection, List<String>> sectionKeyPhrases,
    int minSections,
    boolean requiresJapaneseOnly
) {}
