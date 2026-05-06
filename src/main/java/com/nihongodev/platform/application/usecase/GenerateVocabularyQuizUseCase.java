package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.GenerateVocabularyQuizCommand;
import com.nihongodev.platform.application.dto.VocabularyQuizDto;
import com.nihongodev.platform.application.dto.VocabularyQuizItemDto;
import com.nihongodev.platform.application.port.in.GenerateVocabularyQuizPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.event.VocabularyQuizGeneratedEvent;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.model.Vocabulary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GenerateVocabularyQuizUseCase implements GenerateVocabularyQuizPort {

    private final VocabularyRepositoryPort vocabularyRepository;
    private final EventPublisherPort eventPublisher;

    public GenerateVocabularyQuizUseCase(VocabularyRepositoryPort vocabularyRepository,
                                         EventPublisherPort eventPublisher) {
        this.vocabularyRepository = vocabularyRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public VocabularyQuizDto generate(UUID userId, GenerateVocabularyQuizCommand command) {
        List<Vocabulary> pool;

        if (command.category() != null && command.level() != null) {
            pool = vocabularyRepository.findByCategoryAndLevel(command.category(), command.level());
        } else if (command.category() != null) {
            pool = vocabularyRepository.findByCategory(command.category());
        } else if (command.level() != null) {
            pool = vocabularyRepository.findByLevel(command.level());
        } else {
            pool = vocabularyRepository.findAll();
        }

        if (pool.size() < 4) {
            throw new BusinessException("Not enough vocabulary to generate quiz (minimum 4 words required)");
        }

        int count = Math.min(command.wordCount(), pool.size());
        List<Vocabulary> selected = selectRandom(pool, count);
        List<VocabularyQuizItemDto> items = selected.stream()
                .map(v -> generateQuizItem(v, pool, command.quizType()))
                .toList();

        List<UUID> vocabIds = selected.stream().map(Vocabulary::getId).toList();
        String categoryName = command.category() != null ? command.category().name() : "ALL";
        String levelName = command.level() != null ? command.level().name() : "ALL";

        eventPublisher.publish("vocabulary-events", VocabularyQuizGeneratedEvent.of(
                userId, command.quizType(), count, categoryName, levelName, vocabIds
        ));

        return new VocabularyQuizDto(command.quizType(), count, items);
    }

    private VocabularyQuizItemDto generateQuizItem(Vocabulary target, List<Vocabulary> pool, String quizType) {
        String question;
        String correctAnswer;

        switch (quizType != null ? quizType.toUpperCase() : "FR_TO_JP") {
            case "JP_TO_FR" -> {
                question = target.getJapanese();
                correctAnswer = target.getFrench();
            }
            case "EN_TO_JP" -> {
                question = target.getEnglish();
                correctAnswer = target.getJapanese();
            }
            case "CONTEXT" -> {
                question = target.getExample() != null ? target.getExample() : target.getJapanese();
                correctAnswer = target.getFrench();
            }
            default -> {
                question = target.getFrench();
                correctAnswer = target.getJapanese();
            }
        }

        List<String> options = generateOptions(target, pool, quizType);
        return new VocabularyQuizItemDto(target.getId(), question, correctAnswer, options);
    }

    private List<String> generateOptions(Vocabulary target, List<Vocabulary> pool, String quizType) {
        List<String> options = new ArrayList<>();
        String correct = getAnswerField(target, quizType);
        options.add(correct);

        List<Vocabulary> others = pool.stream()
                .filter(v -> !v.getId().equals(target.getId()))
                .toList();

        List<Vocabulary> shuffled = new ArrayList<>(others);
        Collections.shuffle(shuffled);

        for (Vocabulary v : shuffled) {
            if (options.size() >= 4) break;
            String option = getAnswerField(v, quizType);
            if (option != null && !options.contains(option)) {
                options.add(option);
            }
        }

        Collections.shuffle(options);
        return options;
    }

    private String getAnswerField(Vocabulary v, String quizType) {
        return switch (quizType != null ? quizType.toUpperCase() : "FR_TO_JP") {
            case "JP_TO_FR", "CONTEXT" -> v.getFrench();
            case "EN_TO_JP" -> v.getJapanese();
            default -> v.getJapanese();
        };
    }

    private List<Vocabulary> selectRandom(List<Vocabulary> pool, int count) {
        List<Vocabulary> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, count);
    }
}
