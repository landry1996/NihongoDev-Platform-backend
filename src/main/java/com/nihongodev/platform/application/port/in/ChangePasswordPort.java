package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.ChangePasswordCommand;

public interface ChangePasswordPort {
    void changePassword(ChangePasswordCommand command);
}
