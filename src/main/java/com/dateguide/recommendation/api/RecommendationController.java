package com.dateguide.recommendation.api;

import com.dateguide.recommendation.application.RecommendService;
import com.dateguide.recommendation.dto.client.RecommendClientRequest;
import com.dateguide.recommendation.dto.client.RecommendClientResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Client 로부터 요청을 받아 LLM 으로 전달 후 jobId 를 반환 (202 Accepted)
 * LLM 으로부터 결과를 받으면 SSE 로 Client 에게 전달
 */
@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendService recommendService;

    public RecommendationController(
            @Qualifier("llmRecommendService") RecommendService recommendService
    ) {
        this.recommendService = recommendService;
    }

    @PostMapping
    public ResponseEntity<RecommendClientResponse> requestRecommendation(@RequestBody RecommendClientRequest recommendClientRequest) {
        return ResponseEntity.accepted()
                .body(recommendService.recommendAsync(recommendClientRequest));
    }
}
