package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.KeigoReportDto;

import java.util.UUID;

public interface GetKeigoReportPort {
    KeigoReportDto getReport(UUID userId);
}
