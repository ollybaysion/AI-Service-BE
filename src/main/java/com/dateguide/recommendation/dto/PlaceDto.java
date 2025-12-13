package com.dateguide.recommendation.dto;

public record PlaceDto(
        int order,
        String name,
        String category,
        String address,
        String reason,
        Integer expectedCost
) {}
