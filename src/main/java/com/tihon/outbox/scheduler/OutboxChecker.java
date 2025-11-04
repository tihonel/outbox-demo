package com.tihon.outbox.scheduler;

import com.tihon.outbox.model.OutboxEntry;
import com.tihon.outbox.processor.EventProcessor;
import com.tihon.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxChecker {
    private final OutboxService outboxService;
    private final List<EventProcessor> processors;

    @Scheduled(fixedRateString = "${quantityMessagesForProcess}", timeUnit = TimeUnit.SECONDS)
    public void takeMessagesInPendingAndProcessing() {
        List<OutboxEntry> outboxEntryList = outboxService.getOutboxMessagesInPendingForProcessing();
        outboxEntryList.forEach(this::processOutboxMessage);
    }

    private void processOutboxMessage(OutboxEntry outboxEntry) {
        processors.stream()
                .filter(x -> x.supports(outboxEntry.getEventType()))
                .findFirst()
                .ifPresent(x -> {
                    try {
                        x.execute(outboxEntry);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
