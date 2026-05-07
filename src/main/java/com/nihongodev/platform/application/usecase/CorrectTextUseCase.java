package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CorrectTextCommand;
import com.nihongodev.platform.application.dto.AnnotationDto;
import com.nihongodev.platform.application.dto.CorrectionScoreDto;
import com.nihongodev.platform.application.dto.CorrectionSessionDto;
import com.nihongodev.platform.application.port.in.CorrectTextPort;
import com.nihongodev.platform.application.port.out.CorrectionRuleRepositoryPort;
import com.nihongodev.platform.application.port.out.CorrectionSessionRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.WeaknessPatternRepositoryPort;
import com.nihongodev.platform.application.service.correction.*;
import com.nihongodev.platform.domain.event.TextCorrectedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CorrectTextUseCase implements CorrectTextPort {

    private final CorrectionSessionRepositoryPort sessionRepository;
    private final CorrectionRuleRepositoryPort ruleRepository;
    private final WeaknessPatternRepositoryPort weaknessRepository;
    private final EventPublisherPort eventPublisher;
    private final CorrectionPipeline pipeline;

    public CorrectTextUseCase(CorrectionSessionRepositoryPort sessionRepository,
                              CorrectionRuleRepositoryPort ruleRepository,
                              WeaknessPatternRepositoryPort weaknessRepository,
                              EventPublisherPort eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.ruleRepository = ruleRepository;
        this.weaknessRepository = weaknessRepository;
        this.eventPublisher = eventPublisher;

        List<CorrectionStep> steps = List.of(
                new GrammarStep(),
                new VocabularyStep(),
                new PolitenessStep(),
                new ClarityStep(),
                new NaturalnessStep(),
                new ProfessionalStep()
        );
        this.pipeline = new CorrectionPipeline(steps, new PatternCorrectionEngine());
    }

    @Override
    @Transactional
    public CorrectionSessionDto correct(UUID userId, CorrectTextCommand command) {
        TextType textType = parseTextType(command.textType());
        JapaneseLevel targetLevel = parseLevel(command.targetLevel());
        CorrectionContext context = CorrectionContext.of(textType, targetLevel);

        List<CorrectionRule> rules = ruleRepository.findAllActive();

        CorrectionPipeline.CorrectionPipelineResult result = pipeline.execute(
                command.text(), context, rules);

        CorrectionSession session = CorrectionSession.create(userId, command.text(), textType, targetLevel);
        session.applyResults(result.score(), result.annotations());

        CorrectionSession saved = sessionRepository.save(session);

        updateWeaknessPatterns(userId, result.annotations());

        eventPublisher.publish("correction-events", TextCorrectedEvent.of(
                userId, saved.getId(), textType.name(),
                result.score().getOverallScore(),
                result.annotations().size(),
                saved.getErrorCount()
        ));

        return mapToDto(saved);
    }

    private void updateWeaknessPatterns(UUID userId, List<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.getSeverity() == Severity.INFO) continue;

            String description = annotation.getCategory().name() + ": " + annotation.getRuleId();
            Optional<WeaknessPattern> existing = weaknessRepository
                    .findByUserIdAndCategoryAndDescription(userId, annotation.getCategory(), description);

            if (existing.isPresent()) {
                WeaknessPattern pattern = existing.get();
                pattern.incrementOccurrence(annotation.getOriginal());
                weaknessRepository.save(pattern);
            } else {
                WeaknessPattern newPattern = WeaknessPattern.create(
                        userId, annotation.getCategory(), description, annotation.getOriginal());
                weaknessRepository.save(newPattern);
            }
        }
    }

    private TextType parseTextType(String type) {
        if (type == null || type.isBlank()) return TextType.FREE_TEXT;
        try { return TextType.valueOf(type.toUpperCase()); }
        catch (IllegalArgumentException e) { return TextType.FREE_TEXT; }
    }

    private JapaneseLevel parseLevel(String level) {
        if (level == null || level.isBlank()) return JapaneseLevel.N3;
        try { return JapaneseLevel.valueOf(level.toUpperCase()); }
        catch (IllegalArgumentException e) { return JapaneseLevel.N3; }
    }

    private CorrectionSessionDto mapToDto(CorrectionSession s) {
        CorrectionScore score = s.getScore();
        CorrectionScoreDto scoreDto = new CorrectionScoreDto(
                score.getGrammarScore(), score.getVocabularyScore(),
                score.getPolitenessScore(), score.getClarityScore(),
                score.getNaturalnessScore(), score.getProfessionalScore(),
                score.getOverallScore()
        );

        List<AnnotationDto> annotationDtos = s.getAnnotations().stream()
                .map(a -> new AnnotationDto(
                        a.getStartOffset(), a.getEndOffset(),
                        a.getSeverity(), a.getCategory(),
                        a.getOriginal(), a.getSuggestion(), a.getExplanation()
                )).toList();

        return new CorrectionSessionDto(
                s.getId(), s.getTextType(), s.getTargetLevel(),
                scoreDto, annotationDtos,
                s.getTotalAnnotations(), s.getErrorCount(),
                s.getWarningCount(), s.getInfoCount(),
                s.getCreatedAt()
        );
    }
}
