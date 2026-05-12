package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.RealContentDto;

import java.util.UUID;

public interface AnnotateContentPort {
    RealContentDto execute(UUID contentId);
}
