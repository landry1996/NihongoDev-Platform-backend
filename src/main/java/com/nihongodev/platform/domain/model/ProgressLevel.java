package com.nihongodev.platform.domain.model;

public enum ProgressLevel {
    BEGINNER(0, 999),
    INTERMEDIATE(1000, 4999),
    ADVANCED(5000, 14999),
    EXPERT(15000, Long.MAX_VALUE);

    private final long minXp;
    private final long maxXp;

    ProgressLevel(long minXp, long maxXp) {
        this.minXp = minXp;
        this.maxXp = maxXp;
    }

    public long getMinXp() { return minXp; }
    public long getMaxXp() { return maxXp; }

    public static ProgressLevel fromXp(long xp) {
        for (ProgressLevel level : values()) {
            if (xp >= level.minXp && xp <= level.maxXp) return level;
        }
        return EXPERT;
    }
}
