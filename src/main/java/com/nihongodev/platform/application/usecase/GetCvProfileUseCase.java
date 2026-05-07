package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CvProfileDto;
import com.nihongodev.platform.application.port.in.GetCvProfilePort;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CvProfile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetCvProfileUseCase implements GetCvProfilePort {

    private final CvProfileRepositoryPort profileRepository;

    public GetCvProfileUseCase(CvProfileRepositoryPort profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public CvProfileDto get(UUID userId) {
        CvProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("CvProfile", "userId", userId));
        return CreateCvProfileUseCase.mapToDto(profile);
    }
}
