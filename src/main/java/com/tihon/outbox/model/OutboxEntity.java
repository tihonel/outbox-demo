package com.tihon.outbox.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.time.Instant;

@Entity(name = "outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class OutboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outbox_seq")
    @SequenceGenerator(name = "outbox_seq", sequenceName = "outbox_id_seq", allocationSize = 10)
    private Long id;

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
