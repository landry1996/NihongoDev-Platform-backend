package com.nihongodev.platform.application.port.in;

import java.util.UUID;

public interface ShowcaseBadgePort {
    void showcase(UUID userId, UUID badgeId);
    void unshowcase(UUID userId, UUID badgeId);
}
