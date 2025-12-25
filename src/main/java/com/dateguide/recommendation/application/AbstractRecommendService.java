package com.dateguide.recommendation.application;

import com.dateguide.recommendation.dto.client.RecommendClientRequest;
import com.dateguide.recommendation.dto.client.RecommendClientResponse;

public abstract class AbstractRecommendService implements RecommendService {

    @Override
    public RecommendClientResponse recommend(RecommendClientRequest request) {
        return new RecommendClientResponse("dummy");
    }

    @Override
    public RecommendClientResponse recommendAsync(RecommendClientRequest request) {
        return new RecommendClientResponse("dummy");
    }
}
