package com.dateguide.recommendation.dto.client;

import com.dateguide.recommendation.dto.llm.RecommendLlmResponse;

import java.time.Instant;

public record RecommendClientResponse(
        String jobId,
        Object data,
        Instant createdAt
) {

    public static RecommendClientResponse from(RecommendLlmResponse llm) {
        if (llm == null) throw new IllegalArgumentException("llm must be required");

        return new RecommendClientResponse(
                llm.jobId(),
                llm.payload(),
                llm.createdAt()
        );
    }

}
