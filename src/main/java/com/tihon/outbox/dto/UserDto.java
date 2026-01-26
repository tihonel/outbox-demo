package com.tihon.outbox.dto;

import java.util.UUID;

public record UserDto(UUID id, String username) {
}
