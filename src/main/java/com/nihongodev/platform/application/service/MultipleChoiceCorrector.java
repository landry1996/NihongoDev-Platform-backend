package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.Question;

public class MultipleChoiceCorrector implements QuestionCorrector {

    @Override
    public boolean correct(Question question, String userAnswer) {
        if (userAnswer == null || question.getCorrectAnswer() == null) return false;
        return question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim());
    }
}
