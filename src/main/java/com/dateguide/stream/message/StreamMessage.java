package com.dateguide.stream.message;

import java.time.Instant;

public record StreamMessage<T>(
        String jobId,
        StreamEventType type,
        String id,
        Instant createdAt,
        T payload
) {
}
