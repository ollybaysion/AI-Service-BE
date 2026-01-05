package com.dateguide.scoring.api.dto;

import jakarta.validation.constraints.NotNull;

public record ScoreOptions(
        @NotNull FeatureWindow window,
        boolean returnBreakdown,
        int topK
) {
    public ScoreOptions {
        if (topK <= 0) topK = 50;
    }
}
