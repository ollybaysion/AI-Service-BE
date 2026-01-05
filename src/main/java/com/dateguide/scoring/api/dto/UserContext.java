package com.dateguide.scoring.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UserContext(
        @NotBlank String userId,
        @NotBlank String area,
        @NotNull LocalDate date,
        @NotNull LocalTime startTime,
        @NotNull BudgetRange budgetRange,
        @NotNull Transportation transportation,
        @NotNull List<String> mood
) {}
