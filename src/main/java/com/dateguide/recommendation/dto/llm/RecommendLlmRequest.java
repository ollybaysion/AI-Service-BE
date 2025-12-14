package com.dateguide.recommendation.dto.llm;

import com.dateguide.llm.dto.LlmRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record RecommendLlmRequest(
        String jobId,
        String userId,
        Instant requestedAt,
        String query,
        Constraints constraints,
        Map<String, Object> context,
        Candidates candidates
) implements LlmRequest {
    public record Constraints(
            String area,
            Integer budgetKRW,
            Integer partySize,
            TimeWindow timeWindow,
            String transport,
            List<String> mustHave,
            List<String> avoid
    ) {}

    public record TimeWindow(
            String startLocalTime,
            String endLocalTime
    ) {}

    public record Candidates(
            List<CandidatePlace> inline,
            String refId
    ) {}

    public record CandidatePlace(
            String placeId,
            String name,
            Double lat,
            Double lng,
            Double rating,
            Integer priceLevel,
            List<String> tags,
            Map<String, Object> meta
    ) {}
}
