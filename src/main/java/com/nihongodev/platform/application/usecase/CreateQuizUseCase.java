package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.AddQuestionCommand;
import com.nihongodev.platform.application.command.CreateQuizCommand;
import com.nihongodev.platform.application.dto.QuestionDto;
import com.nihongodev.platform.application.dto.QuizDto;
import com.nihongodev.platform.application.port.in.CreateQuizPort;
import com.nihongodev.platform.application.port.out.QuestionRepositoryPort;
import com.nihongodev.platform.application.port.out.QuizRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Question;
import com.nihongodev.platform.domain.model.Quiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class CreateQuizUseCase implements CreateQuizPort {

    private final QuizRepositoryPort quizRepository;
    private final QuestionRepositoryPort questionRepository;

    public CreateQuizUseCase(QuizRepositoryPort quizRepository, QuestionRepositoryPort questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public QuizDto create(CreateQuizCommand command) {
        Quiz quiz = Quiz.create(
                command.lessonId(), command.title(), command.description(),
                command.level(), command.mode(), command.timeLimitSeconds(),
                command.maxAttempts(), command.passingScore()
        );
        Quiz saved = quizRepository.save(quiz);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public QuestionDto addQuestion(AddQuestionCommand command) {
        if (!quizRepository.existsById(command.quizId())) {
            throw new ResourceNotFoundException("Quiz", "id", command.quizId());
        }

        Question question = Question.create(
                command.quizId(), command.content(), command.correctAnswer(),
                command.explanation(), command.questionType(), command.difficultyLevel(),
                command.options(), command.points(), command.timeLimitSeconds(), command.orderIndex()
        );
        Question saved = questionRepository.save(question);
        return new QuestionDto(
                saved.getId(), saved.getContent(), saved.getQuestionType(),
                saved.getDifficultyLevel(), saved.getOptions(), saved.getPoints(),
                saved.getTimeLimitSeconds(), saved.getOrderIndex()
        );
    }

    private QuizDto mapToDto(Quiz quiz) {
        return new QuizDto(
                quiz.getId(), quiz.getLessonId(), quiz.getTitle(), quiz.getDescription(),
                quiz.getLevel(), quiz.getMode(), quiz.getTimeLimitSeconds(),
                quiz.getMaxAttempts(), quiz.getPassingScore(), quiz.isPublished(),
                quiz.getQuestions().size(), Collections.emptyList(), quiz.getCreatedAt()
        );
    }
}
