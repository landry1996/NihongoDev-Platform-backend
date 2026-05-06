package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.QuestionDto;
import com.nihongodev.platform.application.dto.QuizDto;
import com.nihongodev.platform.application.port.in.GetQuizPort;
import com.nihongodev.platform.application.port.out.QuestionRepositoryPort;
import com.nihongodev.platform.application.port.out.QuizRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Question;
import com.nihongodev.platform.domain.model.Quiz;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetQuizUseCase implements GetQuizPort {

    private final QuizRepositoryPort quizRepository;
    private final QuestionRepositoryPort questionRepository;

    public GetQuizUseCase(QuizRepositoryPort quizRepository, QuestionRepositoryPort questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public QuizDto getById(UUID id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        List<Question> questions = questionRepository.findByQuizId(id);
        return mapToDto(quiz, questions);
    }

    @Override
    public List<QuizDto> getByLessonId(UUID lessonId) {
        return quizRepository.findByLessonId(lessonId).stream()
                .map(q -> mapToDto(q, questionRepository.findByQuizId(q.getId())))
                .toList();
    }

    @Override
    public List<QuizDto> getPublished() {
        return quizRepository.findPublished().stream()
                .map(q -> mapToDto(q, questionRepository.findByQuizId(q.getId())))
                .toList();
    }

    private QuizDto mapToDto(Quiz quiz, List<Question> questions) {
        List<QuestionDto> questionDtos = questions.stream()
                .map(q -> new QuestionDto(q.getId(), q.getContent(), q.getQuestionType(),
                        q.getDifficultyLevel(), q.getOptions(), q.getPoints(),
                        q.getTimeLimitSeconds(), q.getOrderIndex()))
                .toList();
        return new QuizDto(
                quiz.getId(), quiz.getLessonId(), quiz.getTitle(), quiz.getDescription(),
                quiz.getLevel(), quiz.getMode(), quiz.getTimeLimitSeconds(),
                quiz.getMaxAttempts(), quiz.getPassingScore(), quiz.isPublished(),
                questions.size(), questionDtos, quiz.getCreatedAt()
        );
    }
}
