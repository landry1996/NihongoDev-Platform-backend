package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.ExportPitchPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.exception.UnauthorizedException;
import com.nihongodev.platform.domain.model.ExportFormat;
import com.nihongodev.platform.domain.model.GeneratedPitch;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExportPitchUseCase implements ExportPitchPort {

    private final GeneratedPitchRepositoryPort pitchRepository;

    public ExportPitchUseCase(GeneratedPitchRepositoryPort pitchRepository) {
        this.pitchRepository = pitchRepository;
    }

    @Override
    public String export(UUID userId, UUID pitchId, ExportFormat format) {
        GeneratedPitch pitch = pitchRepository.findById(pitchId)
                .orElseThrow(() -> new ResourceNotFoundException("GeneratedPitch", "id", pitchId));

        if (!pitch.getUserId().equals(userId)) {
            throw new UnauthorizedException("You do not own this pitch");
        }

        return switch (format) {
            case MARKDOWN -> pitch.getContent();
            case PLAIN_TEXT -> stripMarkdown(pitch.getContent());
        };
    }

    private String stripMarkdown(String markdown) {
        return markdown
                .replaceAll("^#{1,6}\\s+", "")
                .replaceAll("(?m)^#{1,6}\\s+", "")
                .replaceAll("\\*\\*(.+?)\\*\\*", "$1")
                .replaceAll("\\*(.+?)\\*", "$1")
                .replaceAll("(?m)^- ", "");
    }
}
