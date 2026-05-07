package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateCvProfileCommand;
import com.nihongodev.platform.application.command.UpdateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;
import com.nihongodev.platform.application.port.in.UpdateCvProfilePort;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.WorkExperience;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UpdateCvProfileUseCase implements UpdateCvProfilePort {

    private final CvProfileRepositoryPort profileRepository;

    public UpdateCvProfileUseCase(CvProfileRepositoryPort profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public CvProfileDto update(UUID userId, UpdateCvProfileCommand command) {
        CvProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("CvProfile", "userId", userId));

        if (command.fullName() != null) profile.setFullName(command.fullName());
        if (command.currentRole() != null) profile.setCurrentRole(command.currentRole());
        if (command.targetRole() != null) profile.setTargetRole(command.targetRole());
        if (command.yearsOfExperience() != null) profile.setYearsOfExperience(command.yearsOfExperience());
        if (command.targetCompanyType() != null) profile.setTargetCompanyType(command.targetCompanyType());
        if (command.techStack() != null) profile.setTechStack(command.techStack());
        if (command.experiences() != null) profile.setExperiences(mapExperiences(command.experiences()));
        if (command.certifications() != null) profile.setCertifications(command.certifications());
        if (command.notableProjects() != null) profile.setNotableProjects(command.notableProjects());
        if (command.motivationJapan() != null) profile.setMotivationJapan(command.motivationJapan());
        if (command.japaneseLevel() != null) profile.setJapaneseLevel(command.japaneseLevel());
        profile.setUpdatedAt(LocalDateTime.now());

        CvProfile saved = profileRepository.save(profile);
        return CreateCvProfileUseCase.mapToDto(saved);
    }

    private List<WorkExperience> mapExperiences(List<CreateCvProfileCommand.WorkExperienceData> data) {
        if (data == null) return List.of();
        return data.stream()
                .map(d -> new WorkExperience(d.company(), d.role(), d.durationMonths(), d.highlights()))
                .toList();
    }
}
