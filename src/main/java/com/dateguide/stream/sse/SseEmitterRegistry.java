package com.dateguide.stream.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

public interface SseEmitterRegistry {

    SseEmitter register(String jobId, Duration timeout);

    Optional<SseEmitter> get(String jobId);

    Optional<SseEmitter> remove(String jobId);

    /**
     * snapshot of current jobIds
     */
    Set<String> jobIds();

    int size();
}
