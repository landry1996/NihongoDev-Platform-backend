package com.nihongodev.platform.domain.model;

public record ScenarioChoice(
        String text,
        String textJp,
        boolean isOptimal,
        int culturalScore,
        String feedbackIfChosen
) {}
