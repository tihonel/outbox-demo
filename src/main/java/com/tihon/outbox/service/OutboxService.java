package com.tihon.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tihon.outbox.events.Event;
import com.tihon.outbox.events.EventType;
import com.tihon.outbox.mapper.EventMapper;
import com.tihon.outbox.model.OutboxEntry;
import com.tihon.outbox.model.OutboxEntryStatus;
import com.tihon.outbox.model.User;
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
    private final EventMapper eventMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveNewMessage(User updatedUser, EventType eventType) {
        try {
            Event event = eventMapper.toEvent(updatedUser, eventType);

            var outboxMessage = OutboxEntry.builder()
                    .payload(new ObjectMapper().writeValueAsString(event))
                    .status(OutboxEntryStatus.PENDING)
                    .eventType(eventType)
                    .timeToSend(Instant.now()).build();

            outboxEntryRepository.save(outboxMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<OutboxEntry> getOutboxMessagesInPendingForProcessing() {
        List<OutboxEntry> entries = outboxEntryRepository.findAndSkipLockedMessages(10);
        entries.forEach(x -> x.setStatus(OutboxEntryStatus.IN_PROGRESS));
        return entries;
    }
}
