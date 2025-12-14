package com.dateguide.messaging.message;

import java.time.Instant;
import java.util.Map;

public record GMessage<T>(
        String gId,
        String gDestination,
        String key,
        Instant createdAt,
        Map<String, String> headers,
        T payload
) {
}
