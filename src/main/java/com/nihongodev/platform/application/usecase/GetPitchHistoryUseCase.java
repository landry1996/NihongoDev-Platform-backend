package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.application.port.in.GetPitchHistoryPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.PitchType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetPitchHistoryUseCase implements GetPitchHistoryPort {

    private final GeneratedPitchRepositoryPort pitchRepository;

    public GetPitchHistoryUseCase(GeneratedPitchRepositoryPort pitchRepository) {
        this.pitchRepository = pitchRepository;
    }

    @Override
    public List<GeneratedPitchDto> getHistory(UUID userId, PitchType type) {
        return pitchRepository.findByUserIdAndPitchType(userId, type).stream()
                .map(p -> new GeneratedPitchDto(p.getId(), p.getPitchType(), p.getContent(), p.getGeneratedAt()))
                .toList();
    }

    @Override
    public GeneratedPitchDto getLatest(UUID userId, PitchType type) {
        return pitchRepository.findLatestByUserIdAndPitchType(userId, type)
                .map(p -> new GeneratedPitchDto(p.getId(), p.getPitchType(), p.getContent(), p.getGeneratedAt()))
                .orElseThrow(() -> new ResourceNotFoundException("GeneratedPitch", "pitchType", type));
    }
}
