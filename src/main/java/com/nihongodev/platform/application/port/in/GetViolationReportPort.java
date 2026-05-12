package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.ViolationReportDto;

import java.util.UUID;

public interface GetViolationReportPort {
    ViolationReportDto getByUserId(UUID userId);
}
