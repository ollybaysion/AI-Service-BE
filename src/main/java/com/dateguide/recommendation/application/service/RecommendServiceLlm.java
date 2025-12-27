package com.dateguide.recommendation.application.service;

import com.dateguide.llm.port.out.LlmRequestPublisher;
import com.dateguide.recommendation.dto.client.RecommendClientRequest;
import com.dateguide.recommendation.dto.client.RecommendClientResponse;
import com.dateguide.recommendation.dto.llm.RecommendLlmRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service("llmRecommendService")
public class RecommendServiceLlm extends AbstractRecommendService {

    private final LlmRequestPublisher llmRequestPublisher;

    public RecommendServiceLlm(LlmRequestPublisher llmRequestPublisher) {
        this.llmRequestPublisher = llmRequestPublisher;
    }

    @Override
    public RecommendClientResponse recommendAsync(RecommendClientRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");

        String jobId = UUID.randomUUID().toString();
        llmRequestPublisher.publish(RecommendLlmRequest.from(request, jobId));
        return new RecommendClientResponse(
                jobId,
                "",
                Instant.now()
                );
    }
}
