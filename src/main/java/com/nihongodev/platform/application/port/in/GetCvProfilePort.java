package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.CvProfileDto;

import java.util.UUID;

public interface GetCvProfilePort {
    CvProfileDto get(UUID userId);
}
