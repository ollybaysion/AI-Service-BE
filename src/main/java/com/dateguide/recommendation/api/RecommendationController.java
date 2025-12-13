package com.dateguide.recommendation.api;

import com.dateguide.recommendation.application.RecommendService;
import com.dateguide.recommendation.dto.RecommendRequest;
import com.dateguide.recommendation.dto.RecommendResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendService recommendService;

    public RecommendationController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @PostMapping
    public RecommendResponse createRecommendation(@RequestBody RecommendRequest recommendRequest) {
        return recommendService.recommend(recommendRequest);
    }
}
