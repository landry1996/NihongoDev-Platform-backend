package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.CreatePublicProfileCommand;
import com.nihongodev.platform.application.dto.PublicProfileDto;

public interface CreatePublicProfilePort {
    PublicProfileDto execute(CreatePublicProfileCommand command);
}
