package com.dateguide.llm.adapter;

import com.dateguide.llm.dto.LlmRequest;
import com.dateguide.llm.port.LlmRequestPublisher;
import com.dateguide.messaging.message.GMessage;
import com.dateguide.messaging.port.MessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * LlmRequest 를 GMessage 로 감싸서 MessagePublisher 를 통해 publish.
 */
@Component
public class LlmRequestPublisherAdapter implements LlmRequestPublisher {

    private final MessagePublisher messagePublisher;
    private final String topic;

    public LlmRequestPublisherAdapter(
            MessagePublisher messagePublisher,
            @Value("${kafka.topic.llmRequest}") String topic
    ) {
        this.messagePublisher = messagePublisher;
        this.topic = topic;
    }

    @Override
    public void publish(LlmRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (request.jobId() == null || request.jobId().isEmpty()) throw new IllegalArgumentException("jobId is required");

        GMessage<LlmRequest> message = new GMessage<>(
                UUID.randomUUID().toString(),
                topic,
                request.jobId(),
                Instant.now(),
                Map.of(),
                request
        );
    }
}
