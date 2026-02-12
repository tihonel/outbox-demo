package com.tihon.outbox.processor;

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

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateEventProcessor implements EventProcessor {
    @Value("${kafka.updateTopic}")
    private String updateTopic;
    private final KafkaTemplate<String, OutboxEntityPayload> kafkaTemplate;
    private final OutboxEntityRepository outboxEntityRepository;

    @Override
    @Transactional
    public void execute(OutboxEntity outboxEntity) {
        try {
            kafkaTemplate.send(updateTopic, outboxEntity.getPayload().userId().toString(), outboxEntity.getPayload()).get();
            outboxEntity.setStatus(OutboxEntityStatus.DONE);
            outboxEntityRepository.save(outboxEntity);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supports(EventType eventType) {
        return eventType == EventType.UPDATE_USER_DATA;
    }
}
