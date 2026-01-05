package com.dateguide.scoring.domain.port.model;

public record PlaceFeatures(
        long placeId,
        boolean missing,
        double trendScore,
        double qualityScore,
        double volatility,
        double freshness
) {
    public static PlaceFeatures missing(long placeId) {
        return new PlaceFeatures(placeId, true, 0.0, 0.0, 0.0, 0.0);
    }
}
