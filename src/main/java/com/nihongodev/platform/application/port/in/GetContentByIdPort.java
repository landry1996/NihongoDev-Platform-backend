package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.RealContentDto;

import java.util.UUID;

public interface GetContentByIdPort {
    RealContentDto execute(UUID contentId);
}
