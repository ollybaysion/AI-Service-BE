package com.dateguide.recommendation.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RecommendRequest(
        @NotBlank String userId,

        @NotBlank String area,

        @NotNull LocalDate date,
        @NotNull LocalTime startTime,

        @NotNull BudgetRange budgetRange,

        @NotNull Transportation transportation,

        List<@NotBlank String> mood
) {
    public enum BudgetRange { LOW, MID, HIGH }
    public enum Transportation { WALK, PUBLIC, CAR }
}
