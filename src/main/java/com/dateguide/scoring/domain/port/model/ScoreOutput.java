package com.dateguide.scoring.domain.port.model;

import com.dateguide.scoring.api.dto.ScoreBreakdown;

public record ScoreOutput(
        long placeId,
        double totalScore,
        ScoreBreakdown breakdown
) {
}
