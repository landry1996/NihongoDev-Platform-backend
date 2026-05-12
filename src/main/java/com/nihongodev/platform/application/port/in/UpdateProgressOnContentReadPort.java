package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.ContentReadCompletedEvent;

public interface UpdateProgressOnContentReadPort {
    void execute(ContentReadCompletedEvent event);
}
