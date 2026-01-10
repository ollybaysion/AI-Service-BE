package com.dateguide.scoring.domain.port.model;

import com.dateguide.scoring.api.dto.ScoreBreakdown;
import com.dateguide.scoring.api.dto.TimeContext;
import com.dateguide.scoring.api.dto.UserContext;
import com.dateguide.scoring.domain.port.ScoreCalculator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Component
@Primary
public class ScoreCalculatorV1 implements ScoreCalculator {

    // TrendScore = 0.70*trend + 0.15*momentum + 0.15*freshness
    private static final double TREND_W_TREND = 0.85;
    private static final double TREND_W_FRESH = 0.15;

    // QualityScore = qualityScore
    // FitScore: rule-based
    // FeasibilityScore: time proximity + freshness
    private static final double FEAS_W_TIME = 0.60;
    private static final double FEAS_W_FRESH = 0.40;

    // RiskPeanlty = 0.70*volatility + 0.30*(1-freshness)
    private static final double RISK_W_VOL = 0.70;
    private static final double RISK_W_STALE = 0.30;

    private static final double MISSING_FEATURES_PENALTY = 0.25;

    @Override
    public ScoreOutput score(long placeId, PlaceFeatures f, UserContext user, TimeContext time) {
        double trendScore = computeTrendScore(f);
        double fitScore = computeFitScore(user);
        double qualityScore = computeQualityScore(f);
        double feasibilityScore = computeFeasibilityScore(user, time, f);
        double riskPenalty = computeRiskPenalty(f);

        double total = trendScore + fitScore + qualityScore + feasibilityScore - riskPenalty;

        if (f.missing()) {
            total -= MISSING_FEATURES_PENALTY;
        }

        ScoreBreakdown breakdown = new ScoreBreakdown(
                trendScore,
                fitScore,
                qualityScore,
                feasibilityScore,
                riskPenalty
        );

        return new ScoreOutput(placeId, total, breakdown);
    }

    private static double computeTrendScore(PlaceFeatures f) {
        double raw = TREND_W_TREND * f.trendScore()
                + TREND_W_FRESH * f.freshness();
        return clamp01(raw);
    }

    private static double computeQualityScore(PlaceFeatures f) {
        return clamp01(f.qualityScore());
    }

    private static double computeFitScore(UserContext user) {
        Set<String> mood = new HashSet<>(user.mood() == null ? Set.of() : user.mood());

        double score = 0.20;

        if (mood.contains("romantic")) score += 0.25;
        if (mood.contains("quiet")) score += 0.25;
        if (mood.contains("active")) score += 0.10;
        if (mood.contains("trendy")) score += 0.15;

        return clamp01(score);
    }

    private static double computeFeasibilityScore(UserContext user, TimeContext time, PlaceFeatures f) {
        double timeProximity = computeTimeProximityScore(time.now().toLocalTime(), user.startTime());
        double raw = FEAS_W_TIME * timeProximity + FEAS_W_FRESH * f.freshness();
        return clamp01(raw);
    }

    private static double computeTimeProximityScore(LocalTime now, LocalTime start) {
        int diffMin = Math.abs(now.toSecondOfDay() - start.toSecondOfDay()) / 60;

        if (diffMin <= 30) return 1.0;
        if (diffMin <= 120) return 0.7;
        return 0.4;
    }

    private static double computeRiskPenalty(PlaceFeatures f) {
        double staleness = 1.0 - clamp01(f.freshness());
        double raw = RISK_W_VOL * clamp01(f.volatility())
                + RISK_W_STALE * staleness;
        return clamp01(raw);
    }

    private static double clamp01(double v) {
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }
}
