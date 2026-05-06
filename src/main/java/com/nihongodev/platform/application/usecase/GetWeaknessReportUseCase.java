package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.WeaknessReportDto;
import com.nihongodev.platform.application.port.in.GetWeaknessReportPort;
import com.nihongodev.platform.application.port.out.CorrectionSessionRepositoryPort;
import com.nihongodev.platform.application.port.out.WeaknessPatternRepositoryPort;
import com.nihongodev.platform.domain.model.CorrectionSession;
import com.nihongodev.platform.domain.model.WeaknessPattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetWeaknessReportUseCase implements GetWeaknessReportPort {

    private final WeaknessPatternRepositoryPort weaknessRepository;
    private final CorrectionSessionRepositoryPort sessionRepository;

    public GetWeaknessReportUseCase(WeaknessPatternRepositoryPort weaknessRepository,
                                    CorrectionSessionRepositoryPort sessionRepository) {
        this.weaknessRepository = weaknessRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public WeaknessReportDto getReport(UUID userId) {
        List<WeaknessPattern> patterns = weaknessRepository.findByUserId(userId);
        List<CorrectionSession> sessions = sessionRepository.findByUserId(userId);

        int totalCorrections = sessions.size();
        double averageScore = sessions.stream()
                .mapToDouble(s -> s.getScore().getOverallScore())
                .average()
                .orElse(0.0);

        List<WeaknessReportDto.WeaknessItemDto> items = patterns.stream()
                .sorted((a, b) -> Integer.compare(b.getOccurrenceCount(), a.getOccurrenceCount()))
                .map(p -> new WeaknessReportDto.WeaknessItemDto(
                        p.getCategory(), p.getPatternDescription(),
                        p.getOccurrenceCount(), p.getLastExample(),
                        p.isRecurring()
                ))
                .toList();

        return new WeaknessReportDto(items, totalCorrections, averageScore);
    }
}
