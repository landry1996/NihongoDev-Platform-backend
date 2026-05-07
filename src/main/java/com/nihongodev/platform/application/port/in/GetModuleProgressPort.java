package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.ModuleProgressDto;
import com.nihongodev.platform.domain.model.ModuleType;
import java.util.List;
import java.util.UUID;

public interface GetModuleProgressPort {
    List<ModuleProgressDto> getAll(UUID userId);
    ModuleProgressDto getByType(UUID userId, ModuleType moduleType);
}
