package com.dateguide.recommendation.dto.client;

import com.dateguide.recommendation.dto.PlaceDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RecommendClientResponse(
        String recommendationId,
        String userId,
        String area,
        LocalDate date,
        LocalTime startTime,

        Summary summary,
        List<PlaceDto> placeList
) {
    public record Summary(
            int totalPlaces,
            String routeHint,
            String concept
    ) {}
}
