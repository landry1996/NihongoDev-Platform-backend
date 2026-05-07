package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;
import com.nihongodev.platform.application.dto.WorkExperienceDto;
import com.nihongodev.platform.application.port.in.CreateCvProfilePort;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.WorkExperience;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CreateCvProfileUseCase implements CreateCvProfilePort {

    private final CvProfileRepositoryPort profileRepository;

    public CreateCvProfileUseCase(CvProfileRepositoryPort profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public CvProfileDto create(UUID userId, CreateCvProfileCommand command) {
        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalStateException("CV profile already exists for this user");
        }

        CvProfile profile = CvProfile.create(userId, command.fullName(), command.targetRole(),
                command.yearsOfExperience(), command.targetCompanyType());
        profile.setCurrentRole(command.currentRole());
        profile.setTechStack(command.techStack() != null ? command.techStack() : List.of());
        profile.setExperiences(mapExperiences(command.experiences()));
        profile.setCertifications(command.certifications() != null ? command.certifications() : List.of());
        profile.setNotableProjects(command.notableProjects() != null ? command.notableProjects() : List.of());
        profile.setMotivationJapan(command.motivationJapan());
        profile.setJapaneseLevel(command.japaneseLevel());

        CvProfile saved = profileRepository.save(profile);
        return mapToDto(saved);
    }

    private List<WorkExperience> mapExperiences(List<CreateCvProfileCommand.WorkExperienceData> data) {
        if (data == null) return List.of();
        return data.stream()
                .map(d -> new WorkExperience(d.company(), d.role(), d.durationMonths(), d.highlights()))
                .toList();
    }

    static CvProfileDto mapToDto(CvProfile p) {
        List<WorkExperienceDto> expDtos = p.getExperiences() != null
                ? p.getExperiences().stream()
                    .map(e -> new WorkExperienceDto(e.getCompany(), e.getRole(), e.getDurationMonths(), e.getHighlights()))
                    .toList()
                : List.of();

        return new CvProfileDto(
                p.getId(), p.getUserId(), p.getFullName(), p.getCurrentRole(), p.getTargetRole(),
                p.getYearsOfExperience(), p.getTargetCompanyType(), p.getTechStack(),
                expDtos, p.getCertifications(), p.getNotableProjects(),
                p.getMotivationJapan(), p.getJapaneseLevel(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
