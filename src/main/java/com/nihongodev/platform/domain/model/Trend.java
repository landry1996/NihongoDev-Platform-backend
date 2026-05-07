package com.nihongodev.platform.domain.model;

public enum Trend {
    IMPROVING,
    STABLE,
    DECLINING;

    public static Trend calculate(double score7Days, double score30Days) {
        if (score7Days > score30Days + 5) return IMPROVING;
        if (score7Days < score30Days - 5) return DECLINING;
        return STABLE;
    }
}
