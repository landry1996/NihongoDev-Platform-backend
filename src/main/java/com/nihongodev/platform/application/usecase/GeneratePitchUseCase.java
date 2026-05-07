package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.GeneratePitchCommand;
import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.application.port.in.GeneratePitchPort;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.application.service.generator.PitchAssembler;
import com.nihongodev.platform.domain.event.PitchGeneratedEvent;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.GeneratedPitch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GeneratePitchUseCase implements GeneratePitchPort {

    private final CvProfileRepositoryPort profileRepository;
    private final GeneratedPitchRepositoryPort pitchRepository;
    private final EventPublisherPort eventPublisher;
    private final PitchAssembler pitchAssembler;

    public GeneratePitchUseCase(CvProfileRepositoryPort profileRepository,
                                GeneratedPitchRepositoryPort pitchRepository,
                                EventPublisherPort eventPublisher,
                                PitchAssembler pitchAssembler) {
        this.profileRepository = profileRepository;
        this.pitchRepository = pitchRepository;
        this.eventPublisher = eventPublisher;
        this.pitchAssembler = pitchAssembler;
    }

    @Override
    @Transactional
    public GeneratedPitchDto generate(UUID userId, GeneratePitchCommand command) {
        CvProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("CvProfile", "userId", userId));

        String content = pitchAssembler.assemble(profile, command.pitchType());

        GeneratedPitch pitch = GeneratedPitch.create(userId, command.pitchType(), content, profile.getId());
        GeneratedPitch saved = pitchRepository.save(pitch);

        eventPublisher.publish("cv-generator-events",
                PitchGeneratedEvent.of(userId, saved.getId(), command.pitchType()));

        return new GeneratedPitchDto(saved.getId(), saved.getPitchType(), saved.getContent(), saved.getGeneratedAt());
    }
}
