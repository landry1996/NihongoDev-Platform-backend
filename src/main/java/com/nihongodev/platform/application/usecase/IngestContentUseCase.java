package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.IngestContentCommand;
import com.nihongodev.platform.application.dto.RealContentDto;
import com.nihongodev.platform.application.port.in.IngestContentPort;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.application.service.realcontent.pipeline.ContentIngestionPipeline;
import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngestContentUseCase implements IngestContentPort {

    private final RealContentRepositoryPort contentRepository;
    private final ContentIngestionPipeline pipeline;

    public IngestContentUseCase(RealContentRepositoryPort contentRepository,
                                ContentIngestionPipeline pipeline) {
        this.contentRepository = contentRepository;
        this.pipeline = pipeline;
    }

    @Override
    @Transactional
    public RealContentDto execute(IngestContentCommand command) {
        if (contentRepository.existsBySourceUrl(command.sourceUrl())) {
            throw new IllegalArgumentException("Content from this URL already exists: " + command.sourceUrl());
        }

        RealContent content = RealContent.ingest(
            command.title(), command.body(), command.sourceUrl(),
            command.source(), command.domain()
        );
        content.setAuthorName(command.authorName());

        pipeline.process(content);

        content = contentRepository.save(content);
        return RealContentMapper.toDto(content);
    }
}
