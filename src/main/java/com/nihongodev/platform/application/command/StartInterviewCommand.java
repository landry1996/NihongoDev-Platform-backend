package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.InterviewDifficulty;
import com.nihongodev.platform.domain.model.InterviewType;

public record StartInterviewCommand(
        InterviewType interviewType,
        InterviewDifficulty difficulty
) {}
