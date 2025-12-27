package com.dateguide.recommendation.dto.llm;

import com.dateguide.llm.dto.LlmResponse;

import java.time.Instant;

public record RecommendLlmResponse(
        String jobId,
        String messageId,
        Instant createdAt,
        Object payload
) implements LlmResponse {
}
