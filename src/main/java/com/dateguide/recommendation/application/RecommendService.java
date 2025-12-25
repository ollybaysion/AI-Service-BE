package com.dateguide.recommendation.application;

import com.dateguide.recommendation.dto.client.RecommendClientRequest;
import com.dateguide.recommendation.dto.client.RecommendClientResponse;

public interface RecommendService {
    /**
     * RecommendClientResponse 를 반환한다.
     */
    RecommendClientResponse recommend(RecommendClientRequest request);

    /**
     * jobId 를 반환한다.
     */
    String recommendAsync(RecommendClientRequest request);
}
