package com.tihon.outbox.processor;

import com.tihon.outbox.model.EventType;
import com.tihon.outbox.model.OutboxEntity;

public interface EventProcessor {
    void execute(OutboxEntity outboxEntity);

    boolean supports(EventType eventType);
}
