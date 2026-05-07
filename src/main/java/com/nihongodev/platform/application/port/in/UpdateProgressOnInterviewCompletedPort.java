package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.InterviewCompletedEvent;

public interface UpdateProgressOnInterviewCompletedPort {
    void execute(InterviewCompletedEvent event);
}
