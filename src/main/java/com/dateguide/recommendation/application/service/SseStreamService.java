package com.dateguide.recommendation.application.service;

import com.dateguide.llm.dto.LlmResponse;
import com.dateguide.recommendation.dto.client.RecommendClientResponse;
import com.dateguide.recommendation.dto.llm.RecommendLlmResponse;
import com.dateguide.recommendation.port.in.LlmResponseUseCase;
import com.dateguide.stream.message.StreamEventType;
import com.dateguide.stream.sse.SseEmitterRegistry;
import com.dateguide.stream.sse.SseMessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.Map;

@Service
public class SseStreamService implements LlmResponseUseCase {

    private final SseEmitterRegistry registry;
    private final SseMessagePublisher publisher;
    private final Duration timeout;

    public SseStreamService(
            SseEmitterRegistry registry,
            SseMessagePublisher publisher,
            @Value("${stream.sse.timeout-ms:600000}") long timeoutMs
    ) {
        this.registry = registry;
        this.publisher = publisher;
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    public SseEmitter open(String jobId, String lastEventId) {
        if (jobId == null || jobId.isBlank()) throw new IllegalArgumentException("jobId must not be blank");

        SseEmitter emitter = registry.register(jobId, timeout);

        publisher.publish(jobId, StreamEventType.ACK,
                lastEventId == null ? null : Map.of("lastEventId", lastEventId)
        );

        return emitter;
    }

    @Override
    public void onResponse(LlmResponse response) {
        if (response == null || response.jobId() == null || response.jobId().isBlank()) {
            return;
        }

        RecommendClientResponse clientResponse = RecommendClientResponse.from((RecommendLlmResponse) response);
        publisher.publish(response.jobId(), StreamEventType.DONE, clientResponse);
    }
}
