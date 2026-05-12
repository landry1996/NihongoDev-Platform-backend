package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreatePublicProfileCommand;
import com.nihongodev.platform.application.dto.PublicProfileDto;
import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ProfileVisibility;
import com.nihongodev.platform.domain.model.PublicProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePublicProfileUseCase")
class CreatePublicProfileUseCaseTest {

    @Mock private PublicProfileRepositoryPort profileRepository;

    private CreatePublicProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreatePublicProfileUseCase(profileRepository);
    }

    @Test
    @DisplayName("should create public profile successfully")
    void shouldCreateProfile() {
        UUID userId = UUID.randomUUID();
        var command = new CreatePublicProfileCommand(
            userId, "Taro Yamada", "taro-yamada", "Backend dev learning Japanese",
            JapaneseLevel.N3, ProfileVisibility.PUBLIC, true,
            "Backend Engineer", "Tokyo", List.of("Java", "Spring Boot")
        );

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(profileRepository.existsBySlug("taro-yamada")).thenReturn(false);
        when(profileRepository.save(any(PublicProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        PublicProfileDto dto = useCase.execute(command);

        assertThat(dto.displayName()).isEqualTo("Taro Yamada");
        assertThat(dto.slug()).isEqualTo("taro-yamada");
        assertThat(dto.visibility()).isEqualTo(ProfileVisibility.PUBLIC);
        assertThat(dto.openToWork()).isTrue();
        assertThat(dto.highlightedSkills()).containsExactly("Java", "Spring Boot");
    }

    @Test
    @DisplayName("should fail if user already has profile")
    void shouldFailIfProfileExists() {
        UUID userId = UUID.randomUUID();
        var command = new CreatePublicProfileCommand(
            userId, "Taro", "taro-slug", null,
            JapaneseLevel.N4, ProfileVisibility.PUBLIC, false,
            null, null, List.of()
        );

        when(profileRepository.findByUserId(userId))
            .thenReturn(Optional.of(PublicProfile.create(userId, "Existing", "existing")));

        assertThatThrownBy(() -> useCase.execute(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already exists");
    }
}
