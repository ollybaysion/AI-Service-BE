package com.dateguide.recommendation.dto.llm;

import com.dateguide.llm.dto.LlmRequest;
import com.dateguide.recommendation.dto.client.RecommendClientRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public record RecommendLlmRequest(
        String jobId,
        String userId,
        Instant requestedAt,
        String query,
        Map<String, Object> attributes
) implements LlmRequest {

    public static RecommendLlmRequest from(RecommendClientRequest client,String jobId) {
        if (client == null) throw new IllegalArgumentException("ClientRequest is null");
        if (jobId == null || jobId.isBlank()) throw new IllegalArgumentException("jobId is required");

        Map<String, Object> attributes = new HashMap<>();

        if (client.area() != null) attributes.put("area", client.area());
        if (client.date() != null) attributes.put("date", client.date());
        if (client.startTime() != null) attributes.put("startTime", client.startTime());
        if (client.budgetRange() != null) attributes.put("budgetRange", client.budgetRange());
        if (client.transportation() != null) attributes.put("transportation", client.transportation());

        return new RecommendLlmRequest(
                jobId,
                client.userId(),
                Instant.now(),
                "Give me answer.",
                attributes
        );
    }
}
