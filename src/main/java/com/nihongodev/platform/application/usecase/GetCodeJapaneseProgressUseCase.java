package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CodeJapaneseProgressDto;
import com.nihongodev.platform.application.port.in.GetCodeJapaneseProgressPort;
import com.nihongodev.platform.application.port.out.CodeJapaneseProgressRepositoryPort;
import com.nihongodev.platform.domain.model.CodeJapaneseProgress;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetCodeJapaneseProgressUseCase implements GetCodeJapaneseProgressPort {

    private final CodeJapaneseProgressRepositoryPort progressRepository;

    public GetCodeJapaneseProgressUseCase(CodeJapaneseProgressRepositoryPort progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public List<CodeJapaneseProgressDto> getByUserId(UUID userId) {
        return progressRepository.findByUserId(userId).stream()
            .map(this::toDto)
            .toList();
    }

    private CodeJapaneseProgressDto toDto(CodeJapaneseProgress p) {
        return new CodeJapaneseProgressDto(
            p.getExerciseType(), p.getExercisesCompleted(), p.getAverageScore(),
            p.getBestScore(), p.getCurrentStreak(), p.getRecurringViolations(),
            p.getLastActivityAt()
        );
    }
}
