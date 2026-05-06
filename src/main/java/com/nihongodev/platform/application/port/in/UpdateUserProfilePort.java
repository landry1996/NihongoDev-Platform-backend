package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.UpdateProfileCommand;
import com.nihongodev.platform.application.dto.UserDto;

public interface UpdateUserProfilePort {
    UserDto updateProfile(UpdateProfileCommand command);
}
