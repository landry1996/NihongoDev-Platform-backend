package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCvProfileUseCase")
class CreateCvProfileUseCaseTest {

    @Mock private CvProfileRepositoryPort profileRepository;

    private CreateCvProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateCvProfileUseCase(profileRepository);
    }

    @Test
    @DisplayName("should create CV profile successfully")
    void shouldCreateProfile() {
        UUID userId = UUID.randomUUID();
        CreateCvProfileCommand command = new CreateCvProfileCommand(
                "Pierre Tchiengue", "Java Dev", "Backend Engineer", 5,
                TargetCompanyType.STARTUP, List.of("Java", "Spring"),
                List.of(), List.of("JLPT N3"), List.of(), "Love Japan", "N3");

        when(profileRepository.existsByUserId(userId)).thenReturn(false);
        when(profileRepository.save(any(CvProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        CvProfileDto dto = useCase.create(userId, command);

        assertThat(dto.fullName()).isEqualTo("Pierre Tchiengue");
        assertThat(dto.targetRole()).isEqualTo("Backend Engineer");
        assertThat(dto.yearsOfExperience()).isEqualTo(5);
        assertThat(dto.targetCompanyType()).isEqualTo(TargetCompanyType.STARTUP);
        assertThat(dto.techStack()).containsExactly("Java", "Spring");
    }

    @Test
    @DisplayName("should fail if profile already exists")
    void shouldFailIfProfileExists() {
        UUID userId = UUID.randomUUID();
        CreateCvProfileCommand command = new CreateCvProfileCommand(
                "Pierre", null, "Dev", 3, TargetCompanyType.STARTUP,
                List.of(), List.of(), List.of(), List.of(), null, null);

        when(profileRepository.existsByUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(userId, command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }
}
