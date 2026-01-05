package com.dateguide.scoring.api.dto;

import java.util.List;

public record ScoreResponse(
        List<ScoredPlace> scoredPlaces,
        ScoreMeta meta
) {
    public record ScoredPlace(
            Long placeId,
            double totalScore,
            ScoreBreakdown breakdown
    ) {}

    public record ScoreMeta(
            String scoringVersion,
            String cache,
            FeatureWindow window
    ) {}
}

