package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.KeigoReportDto;
import com.nihongodev.platform.application.port.in.GetKeigoReportPort;
import com.nihongodev.platform.application.port.out.CulturalProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.ScenarioAttemptRepositoryPort;
import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.KeigoViolation;
import com.nihongodev.platform.domain.model.ScenarioAttempt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GetKeigoReportUseCase implements GetKeigoReportPort {

    private final ScenarioAttemptRepositoryPort attemptRepository;
    private final CulturalProgressRepositoryPort progressRepository;

    public GetKeigoReportUseCase(ScenarioAttemptRepositoryPort attemptRepository,
                                 CulturalProgressRepositoryPort progressRepository) {
        this.attemptRepository = attemptRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public KeigoReportDto getReport(UUID userId) {
        List<ScenarioAttempt> attempts = attemptRepository.findByUserId(userId);

        int totalAttempts = attempts.size();
        int averageKeigoScore = totalAttempts == 0 ? 0 :
                (int) attempts.stream()
                        .filter(a -> a.getScore() != null)
                        .mapToInt(a -> a.getScore().keigoScore())
                        .average()
                        .orElse(0);

        List<KeigoViolation> allViolations = attempts.stream()
                .filter(a -> a.getViolations() != null)
                .flatMap(a -> a.getViolations().stream())
                .toList();

        Map<String, Integer> violationsByRule = allViolations.stream()
                .collect(Collectors.groupingBy(KeigoViolation::rule, Collectors.summingInt(v -> 1)));

        List<KeigoViolation> frequentViolations = allViolations.stream()
                .collect(Collectors.groupingBy(KeigoViolation::rule))
                .values().stream()
                .filter(list -> list.size() >= 2)
                .map(list -> list.get(0))
                .limit(10)
                .toList();

        KeigoLevel currentLevel = progressRepository.findByUserId(userId).stream()
                .map(p -> p.getUnlockedLevel())
                .max(Comparator.comparingInt(Enum::ordinal))
                .orElse(KeigoLevel.TEINEIGO);

        return new KeigoReportDto(totalAttempts, averageKeigoScore, currentLevel, frequentViolations, violationsByRule);
    }
}
