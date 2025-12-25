package com.dateguide.recommendation.application;

import com.dateguide.recommendation.dto.client.RecommendClientRequest;
import com.dateguide.recommendation.dto.client.RecommendClientResponse;

public interface RecommendService {
    RecommendClientResponse recommend(RecommendClientRequest recommendClientRequest);
}
