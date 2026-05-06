package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.Question;

public class TextInputCorrector implements QuestionCorrector {

    @Override
    public boolean correct(Question question, String userAnswer) {
        if (userAnswer == null || question.getCorrectAnswer() == null) return false;
        String expected = normalize(question.getCorrectAnswer());
        String actual = normalize(userAnswer);
        return expected.equals(actual);
    }

    private String normalize(String text) {
        return text.trim().toLowerCase()
                .replaceAll("[\\s　]+", " ")
                .replaceAll("[。、.,]", "");
    }
}
