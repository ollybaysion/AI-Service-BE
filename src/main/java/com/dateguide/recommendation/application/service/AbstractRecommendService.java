package com.dateguide.recommendation.application.service;

import com.dateguide.recommendation.dto.client.RecommendClientRequest;
import com.dateguide.recommendation.dto.client.RecommendClientResponse;
import com.dateguide.recommendation.port.in.ClientRecommendUseCase;

import java.time.Instant;

public abstract class AbstractRecommendService implements ClientRecommendUseCase {

    @Override
    public RecommendClientResponse recommend(RecommendClientRequest request) {
        return new RecommendClientResponse(
                "dummy",
                null,
                Instant.now()
        );
    }

    @Override
    public RecommendClientResponse recommendAsync(RecommendClientRequest request) {
        return new RecommendClientResponse(
                "dummy",
                null,
                Instant.now()
        );
    }
}
