package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.ModuleProgressDto;
import com.nihongodev.platform.application.port.in.GetModuleProgressPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.domain.model.ModuleProgress;
import com.nihongodev.platform.domain.model.ModuleType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetModuleProgressUseCase implements GetModuleProgressPort {

    private final ModuleProgressRepositoryPort moduleProgressRepository;

    public GetModuleProgressUseCase(ModuleProgressRepositoryPort moduleProgressRepository) {
        this.moduleProgressRepository = moduleProgressRepository;
    }

    @Override
    public List<ModuleProgressDto> getAll(UUID userId) {
        return moduleProgressRepository.findAllByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ModuleProgressDto getByType(UUID userId, ModuleType moduleType) {
        ModuleProgress mp = moduleProgressRepository.findByUserIdAndModuleType(userId, moduleType)
                .orElseGet(() -> ModuleProgress.initialize(userId, moduleType));
        return toDto(mp);
    }

    private ModuleProgressDto toDto(ModuleProgress mp) {
        return new ModuleProgressDto(
                mp.getModuleType(),
                mp.getCompletedItems(),
                mp.getTotalItems(),
                mp.getCompletionPercentage(),
                mp.getAverageScore(),
                mp.getBestScore(),
                mp.getStatus(),
                mp.getLastCompletedAt()
        );
    }
}
