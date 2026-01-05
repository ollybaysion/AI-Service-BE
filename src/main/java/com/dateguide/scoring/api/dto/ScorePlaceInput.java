package com.dateguide.scoring.api.dto;

import jakarta.validation.constraints.NotNull;

public record ScorePlaceInput(
        @NotNull Long placeId
) {
}
