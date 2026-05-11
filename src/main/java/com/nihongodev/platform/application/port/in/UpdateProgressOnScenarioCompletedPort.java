package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.ScenarioCompletedEvent;

public interface UpdateProgressOnScenarioCompletedPort {
    void execute(ScenarioCompletedEvent event);
}
