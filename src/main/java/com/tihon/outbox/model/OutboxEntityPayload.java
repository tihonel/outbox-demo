package com.tihon.outbox.model;

import java.util.Map;

public record OutboxEntityPayload(Long userId, Map<String, String> changes) {
}
