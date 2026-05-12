package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.ViolationReportDto;
import com.nihongodev.platform.application.port.in.GetViolationReportPort;
import com.nihongodev.platform.application.port.out.CodeJapaneseProgressRepositoryPort;
import com.nihongodev.platform.domain.model.CodeJapaneseProgress;
import com.nihongodev.platform.domain.model.ViolationType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GetViolationReportUseCase implements GetViolationReportPort {

    private final CodeJapaneseProgressRepositoryPort progressRepository;

    public GetViolationReportUseCase(CodeJapaneseProgressRepositoryPort progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public ViolationReportDto getByUserId(UUID userId) {
        List<CodeJapaneseProgress> progressList = progressRepository.findByUserId(userId);

        Map<ViolationType, Integer> aggregated = new HashMap<>();
        for (CodeJapaneseProgress p : progressList) {
            if (p.getRecurringViolations() != null) {
                p.getRecurringViolations().forEach((type, count) ->
                    aggregated.merge(type, count, Integer::sum));
            }
        }

        int total = aggregated.values().stream().mapToInt(Integer::intValue).sum();
        ViolationType mostCommon = aggregated.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        return new ViolationReportDto(aggregated, total, mostCommon);
    }
}
