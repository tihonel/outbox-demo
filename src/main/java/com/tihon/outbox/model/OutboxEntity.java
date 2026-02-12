package com.tihon.outbox.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
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

    @Column(name = "time_created")
    private Instant timeCreated;

    @Column(name = "time_updated")
    private Instant timeUpdated;

    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private EventType eventType;
}
