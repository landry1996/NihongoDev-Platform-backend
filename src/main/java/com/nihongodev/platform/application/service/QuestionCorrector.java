package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.Question;

public interface QuestionCorrector {
    boolean correct(Question question, String userAnswer);
}
