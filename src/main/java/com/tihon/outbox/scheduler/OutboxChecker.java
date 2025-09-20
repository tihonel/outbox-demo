package com.tihon.outbox.scheduler;

import com.tihon.outbox.events.EventType;
import com.tihon.outbox.model.OutboxEntry;
import com.tihon.outbox.processor.EventProcessor;
import com.tihon.outbox.repository.OutboxEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxChecker {
    private final OutboxEntryRepository outboxEntryRepository;
    private final Map<EventType, EventProcessor> processors;

    @Async("forOutbox")
    @Scheduled(fixedRateString = "${outbox.period}", timeUnit = TimeUnit.SECONDS)
    @Transactional
    public void checkOutbox() {
        log.info("Checking outbox {}", Thread.currentThread().getName());
        List<OutboxEntry> outboxEntryList = outboxEntryRepository.findAndSkipLockedMessages(10);
        outboxEntryList.forEach(outboxEntry -> processors.get(outboxEntry.getEventType()).execute(outboxEntry));
        log.info("outboxMessageList: {} ", outboxEntryList);
    }
}
