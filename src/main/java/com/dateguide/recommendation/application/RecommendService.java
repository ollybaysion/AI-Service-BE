package com.dateguide.recommendation.application;

import com.dateguide.recommendation.dto.RecommendRequest;
import com.dateguide.recommendation.dto.RecommendResponse;

public interface RecommendService {
    RecommendResponse recommend(RecommendRequest recommendRequest);
}
