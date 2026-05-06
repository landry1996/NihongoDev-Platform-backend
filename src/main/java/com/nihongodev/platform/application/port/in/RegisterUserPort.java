package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.RegisterCommand;
import com.nihongodev.platform.application.dto.AuthResponse;

public interface RegisterUserPort {
    AuthResponse register(RegisterCommand command);
}
