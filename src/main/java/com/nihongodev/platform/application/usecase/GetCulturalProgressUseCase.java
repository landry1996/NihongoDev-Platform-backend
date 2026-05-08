package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CulturalProgressDto;
import com.nihongodev.platform.application.port.in.GetCulturalProgressPort;
import com.nihongodev.platform.application.port.out.CulturalProgressRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetCulturalProgressUseCase implements GetCulturalProgressPort {

    private final CulturalProgressRepositoryPort progressRepository;

    public GetCulturalProgressUseCase(CulturalProgressRepositoryPort progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public List<CulturalProgressDto> getProgress(UUID userId) {
        return progressRepository.findByUserId(userId).stream()
                .map(p -> new CulturalProgressDto(
                        p.getCategory(), p.getScenariosCompleted(), p.getAverageScore(),
                        p.getBestScore(), p.getCurrentStreak(), p.getUnlockedLevel(),
                        p.getLastActivityAt()
                ))
                .toList();
    }
}
