package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.model.ExportFormat;

import java.util.UUID;

public interface ExportPitchPort {
    String export(UUID userId, UUID pitchId, ExportFormat format);
}
