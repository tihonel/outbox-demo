package com.tihon.outbox.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "user_table")
@Table(name = "user_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;
}
