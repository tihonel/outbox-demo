package com.tihon.outbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDtoWithoutId(@NotNull @NotBlank String username) {
}
