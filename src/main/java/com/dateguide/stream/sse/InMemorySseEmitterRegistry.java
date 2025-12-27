package com.dateguide.stream.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySseEmitterRegistry implements SseEmitterRegistry {

    private final ConcurrentHashMap<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    @Override
    public SseEmitter register(String jobId, Duration timeout) {
        if (jobId == null || jobId.isBlank()) throw new IllegalArgumentException("jobId must not be blank");

        if (timeout == null || timeout.isNegative() || timeout.isZero()) throw new IllegalArgumentException("timeout must be positive");

        SseEmitter emitter = new SseEmitter(timeout.toMillis());

        SseEmitter old = emitterMap.put(jobId, emitter);
        if (old != null) {
            safeComplete(old);
        }

        // lifecycle hooks
        emitter.onCompletion(() -> {
            emitterMap.remove(jobId, emitter);
        });

        emitter.onTimeout(() -> {
            emitterMap.remove(jobId, emitter);
            safeComplete(emitter);
        });

        emitter.onError(ex -> {
            emitterMap.remove(jobId, emitter);
            safeComplete(emitter);
        });

        return emitter;
    }

    @Override
    public Optional<SseEmitter> get(String jobId) {
        if (jobId == null) return Optional.empty();
        return Optional.ofNullable(emitterMap.get(jobId));
    }

    @Override
    public Optional<SseEmitter> remove(String jobId) {
        if (jobId == null) return Optional.empty();
        return Optional.ofNullable(emitterMap.remove(jobId));
    }

    @Override
    public Set<String> jobIds() {
        return Set.copyOf(emitterMap.keySet());
    }

    @Override
    public int size() {
        return emitterMap.size();
    }

    private void safeComplete(SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (Exception ignore) {

        }
    }

}
