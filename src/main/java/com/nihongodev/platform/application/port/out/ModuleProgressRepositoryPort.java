package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.ModuleProgress;
import com.nihongodev.platform.domain.model.ModuleType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleProgressRepositoryPort {
    ModuleProgress save(ModuleProgress moduleProgress);
    Optional<ModuleProgress> findByUserIdAndModuleType(UUID userId, ModuleType moduleType);
    List<ModuleProgress> findAllByUserId(UUID userId);
}
