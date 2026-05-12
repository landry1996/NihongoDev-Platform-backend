package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.RealContentDto;
import com.nihongodev.platform.application.port.in.AnnotateContentPort;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.application.service.realcontent.annotator.AnnotationEngine;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AnnotateContentUseCase implements AnnotateContentPort {

    private final RealContentRepositoryPort contentRepository;
    private final AnnotationEngine annotationEngine;

    public AnnotateContentUseCase(RealContentRepositoryPort contentRepository,
                                  AnnotationEngine annotationEngine) {
        this.contentRepository = contentRepository;
        this.annotationEngine = annotationEngine;
    }

    @Override
    @Transactional
    public RealContentDto execute(UUID contentId) {
        RealContent content = contentRepository.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("RealContent", "id", contentId));

        annotationEngine.annotate(content);
        content = contentRepository.save(content);
        return RealContentMapper.toDto(content);
    }
}
