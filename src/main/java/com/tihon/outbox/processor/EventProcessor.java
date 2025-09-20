package com.tihon.outbox.processor;

import com.tihon.outbox.model.OutboxEntry;

public interface EventProcessor {
    void execute(OutboxEntry outboxEntry);
}
