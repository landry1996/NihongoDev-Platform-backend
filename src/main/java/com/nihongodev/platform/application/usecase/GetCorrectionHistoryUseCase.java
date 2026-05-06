package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.AnnotationDto;
import com.nihongodev.platform.application.dto.CorrectionScoreDto;
import com.nihongodev.platform.application.dto.CorrectionSessionDto;
import com.nihongodev.platform.application.port.in.GetCorrectionHistoryPort;
import com.nihongodev.platform.application.port.out.CorrectionSessionRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CorrectionScore;
import com.nihongodev.platform.domain.model.CorrectionSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetCorrectionHistoryUseCase implements GetCorrectionHistoryPort {

    private final CorrectionSessionRepositoryPort sessionRepository;

    public GetCorrectionHistoryUseCase(CorrectionSessionRepositoryPort sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<CorrectionSessionDto> getUserHistory(UUID userId) {
        return sessionRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public CorrectionSessionDto getSessionById(UUID userId, UUID sessionId) {
        CorrectionSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("CorrectionSession", "id", sessionId));

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized access to correction session");
        }

        return mapToDto(session);
    }

    private CorrectionSessionDto mapToDto(CorrectionSession s) {
        CorrectionScore score = s.getScore();
        CorrectionScoreDto scoreDto = new CorrectionScoreDto(
                score.getGrammarScore(), score.getVocabularyScore(),
                score.getPolitenessScore(), score.getClarityScore(),
                score.getNaturalnessScore(), score.getProfessionalScore(),
                score.getOverallScore()
        );

        List<AnnotationDto> annotationDtos = s.getAnnotations() != null
                ? s.getAnnotations().stream()
                .map(a -> new AnnotationDto(
                        a.getStartOffset(), a.getEndOffset(),
                        a.getSeverity(), a.getCategory(),
                        a.getOriginal(), a.getSuggestion(), a.getExplanation()
                )).toList()
                : List.of();

        return new CorrectionSessionDto(
                s.getId(), s.getTextType(), s.getTargetLevel(),
                scoreDto, annotationDtos,
                s.getTotalAnnotations(), s.getErrorCount(),
                s.getWarningCount(), s.getInfoCount(),
                s.getCreatedAt()
        );
    }
}
