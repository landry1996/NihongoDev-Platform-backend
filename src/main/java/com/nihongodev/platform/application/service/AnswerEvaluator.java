package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.InterviewFeedback;
import com.nihongodev.platform.domain.model.InterviewQuestion;

public interface AnswerEvaluator {
    InterviewFeedback evaluate(InterviewQuestion question, String answerText, int timeSpentSeconds);
}
