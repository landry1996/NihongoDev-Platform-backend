package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.WeaknessReportDto;

import java.util.UUID;

public interface GetWeaknessReportPort {
    WeaknessReportDto getReport(UUID userId);
}
