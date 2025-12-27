package com.dateguide.stream.sse;

import com.dateguide.stream.message.StreamEventType;
import com.dateguide.stream.message.StreamMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.UUID;

@Component
public class SseMessagePublisher {

    private final SseEmitterRegistry registry;

    public SseMessagePublisher(SseEmitterRegistry registry) {
        this.registry = registry;
    }

    public <T> boolean publish(String jobId, StreamEventType type, T payload) {
        if (jobId == null || jobId.isBlank()) throw new IllegalArgumentException("jobId must not be blank");
        if (type == null) throw new IllegalArgumentException("type must not be null");

        String eventId = UUID.randomUUID().toString();

        StreamMessage<T> streamMessage = new StreamMessage<>(
                jobId,
                type,
                eventId,
                Instant.now(),
                payload
        );

        return send(streamMessage);
    }

    private boolean send(StreamMessage<?> message) {
        return registry.get(message.jobId())
                .map(emitter -> {
                    try {
                        emitter.send(
                                SseEmitter.event()
                                        .name(message.type().sseEventName())
                                        .id(message.id())
                                        .data(message)
                        );

                        if (message.type().isTerminal()) {
                            registry.remove(message.jobId());
                            emitter.complete();
                        }
                        return true;
                    } catch (Exception e) {
                        registry.remove(message.jobId());
                        try { emitter.complete(); } catch (Exception ignore) {}
                        return false;
                    }
                })
                .orElse(false);
    }
}
