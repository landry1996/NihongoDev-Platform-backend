package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.DeleteLessonPort;
import com.nihongodev.platform.application.port.out.LessonRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteLessonUseCase implements DeleteLessonPort {

    private final LessonRepositoryPort lessonRepository;

    public DeleteLessonUseCase(LessonRepositoryPort lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lesson", "id", id);
        }
        lessonRepository.deleteById(id);
    }
}
