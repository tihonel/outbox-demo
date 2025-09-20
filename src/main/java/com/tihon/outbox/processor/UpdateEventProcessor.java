package com.tihon.outbox.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tihon.outbox.events.UpdateUserEvent;
import com.tihon.outbox.exception.UnsuccessfulSendEventToKafka;
import com.tihon.outbox.model.OutboxEntry;
import com.tihon.outbox.model.OutboxEntryStatus;
import com.tihon.outbox.repository.OutboxEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateEventProcessor implements EventProcessor {
    @Value("${kafka.updateTopic}")
    private String updateTopic;
    @Value("${outbox.period}")
    private int outboxPeriod;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxEntryRepository outboxEntryRepository;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void execute(OutboxEntry outboxEntry) {
        try {
            mapper.readValue(outboxEntry.getPayload(), UpdateUserEvent.class);
            kafkaTemplate.send(updateTopic, outboxEntry.getPayload());
            outboxEntry.setStatus(OutboxEntryStatus.COMPLETED);
            outboxEntryRepository.save(outboxEntry);
        } catch (Exception e) {
            outboxEntry.setTimeToSend(Instant.now().plus(outboxPeriod, ChronoUnit.SECONDS));
            outboxEntryRepository.save(outboxEntry);
            log.error("Failed to send event to kafka. Event {} ", outboxEntry.getPayload(), e);
            throw new UnsuccessfulSendEventToKafka(e.getMessage(), e);
        }
    }
}
