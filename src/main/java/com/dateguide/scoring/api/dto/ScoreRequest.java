package com.dateguide.scoring.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ScoreRequest(
        @NotNull
        @Valid
        UserContext userContext,
        @NotNull
        @Valid
        TimeContext timeContext,
        @NotEmpty
        List<@Valid ScorePlaceInput> places,
        @NotNull
        @Valid
        ScoreOptions options
) {

}
