package com.nihongodev.platform.application.port.in;

import java.util.UUID;

public interface LogoutPort {
    void logout(UUID userId);
}
