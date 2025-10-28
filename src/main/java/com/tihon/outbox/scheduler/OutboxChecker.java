package com.tihon.outbox.scheduler;

import com.tihon.outbox.events.EventType;
import com.tihon.outbox.model.OutboxEntry;
import com.tihon.outbox.processor.EventProcessor;
import com.tihon.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxChecker {
    private final OutboxService outboxService;
    private final Map<EventType, EventProcessor> processors;

    @Async("forOutbox")
    @Scheduled(fixedRateString = "${outbox.period}", timeUnit = TimeUnit.SECONDS)
    public void takeMessagesInPendingAndProcessing() {
        List<OutboxEntry> outboxEntryList = outboxService.getOutboxMessagesInPendingForProcessing();
        outboxEntryList.forEach(outboxEntry -> processors.get(outboxEntry.getEventType()).execute(outboxEntry));
    }
}
