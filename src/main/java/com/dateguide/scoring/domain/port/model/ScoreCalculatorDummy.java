package com.dateguide.scoring.domain.port.model;

import com.dateguide.scoring.api.dto.ScoreBreakdown;
import com.dateguide.scoring.api.dto.TimeContext;
import com.dateguide.scoring.api.dto.UserContext;
import com.dateguide.scoring.domain.port.ScoreCalculator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Set;

@Component
@Primary
public class ScoreCalculatorDummy implements ScoreCalculator {

    @Override
    public ScoreOutput score(long placeId, PlaceFeatures f, UserContext user, TimeContext time) {
        double trendScore = f.trendScore();

        double qualityScore = f.qualityScore();

        double riskPenalty = f.volatility();

        double fitScore = computeFitScore(user);

        double feasibilityScore = computeFeasibilityScore(user, time, f);

        double total = trendScore + fitScore + qualityScore + feasibilityScore - riskPenalty;

        ScoreBreakdown breakdown = new ScoreBreakdown(
                trendScore, fitScore, qualityScore, feasibilityScore, riskPenalty
        );

        return new ScoreOutput(placeId, total, breakdown);
    }

    private static double computeFitScore(UserContext user) {
        Set<String> m = Set.copyOf(user.mood());
        double score = 0.2;

        if (m.contains("romantic")) score += 0.3;
        if (m.contains("quiet")) score += 0.3;
        if (m.contains("active")) score += 0.2;

        return clamp01(score);
    }

    private static double computeFeasibilityScore(UserContext user, TimeContext time, PlaceFeatures f) {
        LocalTime now  = time.now().toLocalTime();
        LocalTime start = user.startTime();

        int diffMin = Math.abs(now.toSecondOfDay() - start.toSecondOfDay()) / 60;

        double timeFit =
                diffMin <= 30 ? 1.0 :
                diffMin <= 120 ? 0.7 :
                0.4;

        double score = 0.6 * timeFit + 0.4 * f.freshness();
        return clamp01(score);
    }

    private static double clamp01(double v) {
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }

}
