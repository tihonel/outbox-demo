package com.tihon.outbox.processor;

import com.tihon.outbox.exception.UnsuccessfulSendEventToKafka;
import com.tihon.outbox.model.EventType;
import com.tihon.outbox.model.OutboxEntity;
import com.tihon.outbox.model.OutboxEntityPayload;
import com.tihon.outbox.model.OutboxEntityStatus;
import com.tihon.outbox.repository.OutboxEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
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
    private final KafkaTemplate<String, OutboxEntityPayload> kafkaTemplate;
    private final OutboxEntityRepository outboxEntityRepository;

    @Override
    @Transactional
    public void execute(OutboxEntity outboxEntity) {
        try {
            kafkaTemplate.send(updateTopic, outboxEntity.getPayload().userId().toString(), outboxEntity.getPayload());
            outboxEntity.setStatus(OutboxEntityStatus.DONE);
            outboxEntityRepository.save(outboxEntity);
        } catch (Exception e) {
            outboxEntity.setTimeToSend(Instant.now().plus(outboxPeriod, ChronoUnit.SECONDS));
            outboxEntityRepository.save(outboxEntity);
            log.error("Failed to send event to kafka. Event {} ", outboxEntity.getPayload(), e);
            throw new UnsuccessfulSendEventToKafka(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(EventType eventType) {
        return eventType == EventType.UPDATE_USER_DATA;
    }
}
