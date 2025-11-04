package com.tihon.outbox.service;

import com.tihon.outbox.model.*;
import com.tihon.outbox.repository.OutboxEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxEntityRepository outboxEntityRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveNewMessage(OutboxEntityPayload payload, EventType eventType) {
        var outboxMessage = OutboxEntity.builder()
                .payload(payload)
                .status(OutboxEntityStatus.PENDING)
                .eventType(eventType)
                .timeToSend(Instant.now()).build();
        outboxEntityRepository.save(outboxMessage);
    }

    @Transactional
    public List<OutboxEntity> getOutboxMessagesInPendingForProcessing() {
        List<OutboxEntity> entries = outboxEntityRepository.findAndSkipLockedMessages(10);
        entries.forEach(x -> x.setStatus(OutboxEntityStatus.RUNNING));
        return entries;
    }
}
