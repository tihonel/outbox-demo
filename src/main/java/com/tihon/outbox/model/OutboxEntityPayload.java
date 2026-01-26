package com.tihon.outbox.model;

import java.util.Map;
import java.util.UUID;

public record OutboxEntityPayload(UUID userId, Map<String, String> changes) {
}
