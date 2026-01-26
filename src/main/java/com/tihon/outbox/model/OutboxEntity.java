package com.tihon.outbox.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "outbox")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Type(JsonType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    private OutboxEntityPayload payload;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OutboxEntityStatus status;

    @Column(name = "time_to_send")
    private Instant timeToSend;

    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private EventType eventType;
}
