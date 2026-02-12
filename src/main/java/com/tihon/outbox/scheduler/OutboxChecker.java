package com.tihon.outbox.scheduler;

import com.tihon.outbox.model.OutboxEntity;
import com.tihon.outbox.processor.EventProcessor;
import com.tihon.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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

    @Async("outboxThreadPool")
    @Scheduled(fixedRateString = "${outbox.period}", timeUnit = TimeUnit.SECONDS)
    public void takeMessagesInPendingAndProcessing() {
        List<OutboxEntity> outboxEntityList = outboxService.getOutboxMessagesInPendingForProcessing();
        outboxEntityList.forEach(this::processOutboxMessage);
    }

    private void processOutboxMessage(OutboxEntity outboxEntity) {
        boolean processed = processors.stream()
                .filter(x -> x.supports(outboxEntity.getEventType()))
                .findFirst()
                .map(x -> {
                    try {
                        x.execute(outboxEntity);
                        return true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(false);

        if(!processed) {
            log.warn("No processor found for eventType: {}", outboxEntity.getEventType());
        }
    }
}
