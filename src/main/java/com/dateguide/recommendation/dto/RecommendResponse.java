package com.dateguide.recommendation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RecommendResponse(
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
