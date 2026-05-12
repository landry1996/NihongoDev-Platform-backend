package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.UpdatePublicProfileCommand;
import com.nihongodev.platform.application.dto.PublicProfileDto;

public interface UpdatePublicProfilePort {
    PublicProfileDto execute(UpdatePublicProfileCommand command);
}
