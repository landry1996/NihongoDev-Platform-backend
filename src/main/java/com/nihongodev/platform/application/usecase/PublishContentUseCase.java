package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.RealContentDto;
import com.nihongodev.platform.application.port.in.PublishContentPort;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PublishContentUseCase implements PublishContentPort {

    private final RealContentRepositoryPort contentRepository;

    public PublishContentUseCase(RealContentRepositoryPort contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    @Transactional
    public RealContentDto execute(UUID contentId) {
        RealContent content = contentRepository.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("RealContent", "id", contentId));

        content.publish();
        content = contentRepository.save(content);
        return RealContentMapper.toDto(content);
    }
}
