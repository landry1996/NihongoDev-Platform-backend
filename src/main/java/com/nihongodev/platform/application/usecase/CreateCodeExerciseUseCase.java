package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateCodeExerciseCommand;
import com.nihongodev.platform.application.dto.CodeReviewExerciseDto;
import com.nihongodev.platform.application.port.in.CreateCodeExercisePort;
import com.nihongodev.platform.application.port.out.CodeExerciseRepositoryPort;
import com.nihongodev.platform.domain.model.CodeReviewExercise;
import org.springframework.stereotype.Service;

@Service
public class CreateCodeExerciseUseCase implements CreateCodeExercisePort {

    private final CodeExerciseRepositoryPort exerciseRepository;

    public CreateCodeExerciseUseCase(CodeExerciseRepositoryPort exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public CodeReviewExerciseDto execute(CreateCodeExerciseCommand command) {
        CodeReviewExercise exercise = CodeReviewExercise.create(
            command.title(), command.titleJp(), command.type(),
            command.codeContext(), command.difficulty(),
            command.scenario(), command.scenarioJp(), command.xpReward()
        );
        exercise.setCodeSnippet(command.codeSnippet());
        exercise.setCodeLanguage(command.codeLanguage());
        exercise.setExpectedReviewLevel(command.expectedReviewLevel());
        exercise.setTechnicalIssues(command.technicalIssues());
        exercise.setModelAnswer(command.modelAnswer());
        exercise.setModelAnswerExplanation(command.modelAnswerExplanation());
        exercise.setKeyPhrases(command.keyPhrases());
        exercise.setAvoidPhrases(command.avoidPhrases());
        exercise.setTechnicalTermsJp(command.technicalTermsJp());
        exercise.setPrTemplate(command.prTemplate());
        exercise.setCommitRule(command.commitRule());
        exercise.setCulturalNote(command.culturalNote());

        CodeReviewExercise saved = exerciseRepository.save(exercise);

        return new CodeReviewExerciseDto(
            saved.getId(), saved.getTitle(), saved.getTitleJp(), saved.getType(),
            saved.getCodeContext(), saved.getDifficulty(), saved.getCodeSnippet(),
            saved.getCodeLanguage(), saved.getScenario(), saved.getScenarioJp(),
            saved.getExpectedReviewLevel(), saved.getTechnicalIssues(), saved.getModelAnswer(),
            saved.getModelAnswerExplanation(), saved.getKeyPhrases(), saved.getAvoidPhrases(),
            saved.getTechnicalTermsJp(), saved.getPrTemplate(), saved.getCommitRule(),
            saved.getCulturalNote(), saved.getXpReward()
        );
    }
}
