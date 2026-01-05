package com.dateguide.scoring.api.dto;

public record ScoreBreakdown(
        double trendScore,
        double fitScore,
        double qualityScore,
        double feasibilityScore,
        double riskPenalty
) {
    public double recomputeTotal() {
        return trendScore + fitScore + qualityScore + feasibilityScore - riskPenalty;
    }
}
