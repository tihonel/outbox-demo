package com.tihon.outbox.service;

import com.tihon.outbox.model.*;
import com.tihon.outbox.repository.OutboxEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {
    @Value("${quantityMessagesForProcess:10}")
    private int quantityMessagesForProcess;
    private final OutboxEntityRepository outboxEntityRepository;
    private final Clock clock;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveNewMessage(OutboxEntityPayload payload, EventType eventType) {
        Instant now = clock.instant();
        var outboxMessage = OutboxEntity.builder()
                .payload(payload)
                .status(OutboxEntityStatus.PENDING)
                .eventType(eventType)
                .timeToSend(now)
                .timeCreated(now)
                .build();
        outboxEntityRepository.save(outboxMessage);
    }

    @Transactional
    public List<OutboxEntity> getOutboxMessagesInPendingForProcessing() {
        List<OutboxEntity> entries = outboxEntityRepository.findAndSkipLockedMessages(quantityMessagesForProcess);
        entries.forEach(x -> x.setStatus(OutboxEntityStatus.RUNNING));
        return outboxEntityRepository.saveAll(entries);
    }
}
