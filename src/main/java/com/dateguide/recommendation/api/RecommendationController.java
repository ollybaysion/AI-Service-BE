package com.dateguide.recommendation.api;

import com.dateguide.recommendation.application.RecommendService;
import com.dateguide.recommendation.dto.client.RecommendClientRequest;
import com.dateguide.recommendation.dto.client.RecommendClientResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendService recommendService;

    public RecommendationController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @PostMapping
    public RecommendClientResponse createRecommendation(@RequestBody RecommendClientRequest recommendClientRequest) {
        return recommendService.recommend(recommendClientRequest);
    }
}
