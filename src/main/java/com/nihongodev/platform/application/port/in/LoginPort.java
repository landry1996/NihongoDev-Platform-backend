package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.LoginCommand;
import com.nihongodev.platform.application.dto.AuthResponse;

public interface LoginPort {
    AuthResponse login(LoginCommand command);
}
