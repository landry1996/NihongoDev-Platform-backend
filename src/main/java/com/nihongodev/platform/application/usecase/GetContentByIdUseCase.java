package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.RealContentDto;
import com.nihongodev.platform.application.port.in.GetContentByIdPort;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetContentByIdUseCase implements GetContentByIdPort {

    private final RealContentRepositoryPort contentRepository;

    public GetContentByIdUseCase(RealContentRepositoryPort contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    public RealContentDto execute(UUID contentId) {
        RealContent content = contentRepository.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("RealContent", "id", contentId));
        return RealContentMapper.toDto(content);
    }
}
