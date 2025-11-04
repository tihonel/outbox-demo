package com.tihon.outbox.model;

import java.util.Map;

public record OutboxEntryPayload(Long userId, Map<String, String> changes) {
}
