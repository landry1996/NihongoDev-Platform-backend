package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.UpdateContentPreferencesCommand;
import com.nihongodev.platform.application.dto.UserContentPreferenceDto;

public interface UpdateContentPreferencesPort {
    UserContentPreferenceDto execute(UpdateContentPreferencesCommand command);
}
