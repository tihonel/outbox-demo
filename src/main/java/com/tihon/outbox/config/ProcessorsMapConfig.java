package com.tihon.outbox.config;

import com.tihon.outbox.model.EventType;
import com.tihon.outbox.processor.EventProcessor;
import com.tihon.outbox.processor.UpdateEventProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
public class ProcessorsMapConfig {
    @Bean
    public Map<EventType, EventProcessor> processorMap(UpdateEventProcessor updateEventProcessor) {
        return Map.of(EventType.UPDATE_USER_DATA, updateEventProcessor);
    }
}
