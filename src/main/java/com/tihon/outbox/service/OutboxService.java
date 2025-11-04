package com.tihon.outbox.service;

import com.tihon.outbox.model.*;
import com.tihon.outbox.repository.OutboxEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxEntryRepository outboxEntryRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveNewMessage(OutboxEntryPayload payload, EventType eventType) {
        var outboxMessage = OutboxEntry.builder()
                .payload(payload)
                .status(OutboxEntryStatus.PENDING)
                .eventType(eventType)
                .timeToSend(Instant.now()).build();
        outboxEntryRepository.save(outboxMessage);
    }

    @Transactional
    public List<OutboxEntry> getOutboxMessagesInPendingForProcessing() {
        List<OutboxEntry> entries = outboxEntryRepository.findAndSkipLockedMessages(10);
        entries.forEach(x -> x.setStatus(OutboxEntryStatus.IN_PROGRESS));
        return entries;
    }
}
