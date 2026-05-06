package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.AuthResponse;

public interface RefreshTokenPort {
    AuthResponse refresh(String refreshToken);
}
