package com.dateguide.recommendation.port.in;

import com.dateguide.recommendation.dto.client.RecommendClientRequest;
import com.dateguide.recommendation.dto.client.RecommendClientResponse;

public interface ClientRecommendUseCase {
    /**
     * RecommendClientResponse 를 반환한다.
     */
    RecommendClientResponse recommend(RecommendClientRequest request);

    /**
     * jobId 를 반환한다.
     */
    RecommendClientResponse recommendAsync(RecommendClientRequest request);
}
