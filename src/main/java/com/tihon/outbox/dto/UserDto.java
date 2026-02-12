package com.tihon.outbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserDto(@NotNull UUID id, @NotNull @NotBlank String username) {
}
