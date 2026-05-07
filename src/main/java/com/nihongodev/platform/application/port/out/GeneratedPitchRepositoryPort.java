package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.GeneratedPitch;
import com.nihongodev.platform.domain.model.PitchType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GeneratedPitchRepositoryPort {
    GeneratedPitch save(GeneratedPitch pitch);
    Optional<GeneratedPitch> findById(UUID id);
    List<GeneratedPitch> findByUserIdAndPitchType(UUID userId, PitchType pitchType);
    Optional<GeneratedPitch> findLatestByUserIdAndPitchType(UUID userId, PitchType pitchType);
}
