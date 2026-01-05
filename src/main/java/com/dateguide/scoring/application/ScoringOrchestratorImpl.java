package com.dateguide.scoring.application;

import com.dateguide.scoring.api.dto.*;
import com.dateguide.scoring.domain.port.FeatureProvider;
import com.dateguide.scoring.domain.port.ScoreCalculator;
import com.dateguide.scoring.domain.port.model.PlaceFeatures;
import com.dateguide.scoring.domain.port.model.ScoreOutput;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ScoringOrchestratorImpl implements ScoringOrchestrator {

    private final FeatureProvider featureProvider;
    private final ScoreCalculator scoreCalculator;

    public ScoringOrchestratorImpl(FeatureProvider featureProvider, ScoreCalculator scoreCalculator) {
        this.featureProvider = featureProvider;
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    public ScoreResponse orchestrate(ScoreRequest request) {
        FeatureWindow window = defaultWindow(request.options());
        int topK = defaultTopK(request.options());
        boolean returnBreakdown = request.options().returnBreakdown();

        List<Long> placeIds = normalizePlaceIds(request.places());
        if (placeIds.isEmpty()) {
            return new ScoreResponse(List.of(),
                    new ScoreResponse.ScoreMeta("v1", "MISS", window));
        }

        FeatureProvider.FeatureFetchResult fetchResult = featureProvider.getFeatures(placeIds, window);
        Map<Long, PlaceFeatures> featureMap = fetchResult.features();

        List<ScoreResponse.ScoredPlace> scored = new ArrayList<>(placeIds.size());
        int missingCount = 0;

        for (Long placeId : placeIds) {
            PlaceFeatures features = featureMap.get(placeId);
            if (features == null) {
                features = PlaceFeatures.missing(placeId);
                missingCount++;
            }

            ScoreOutput out = scoreCalculator.score(placeId, features, request.userContext(), request.timeContext());

            ScoreBreakdown breakdown = returnBreakdown
                    ? new ScoreBreakdown(
                    out.breakdown().trendScore(),
                    out.breakdown().fitScore(),
                    out.breakdown().qualityScore(),
                    out.breakdown().feasibilityScore(),
                    out.breakdown().riskPenalty()
                    )
                    : null;

            scored.add(new ScoreResponse.ScoredPlace(placeId, out.totalScore(), breakdown));
        }

        List<ScoreResponse.ScoredPlace> top = scored.stream()
                .sorted(Comparator
                        .comparingDouble(ScoreResponse.ScoredPlace::totalScore).reversed()
                        .thenComparing(ScoreResponse.ScoredPlace::placeId))
                .limit(topK)
                .toList();

        String cache = fetchResult.cacheStatus().name();
        ScoreResponse.ScoreMeta meta = new ScoreResponse.ScoreMeta("v1", cache, window);

        return new ScoreResponse(top, meta);
    }

    private static List<Long> normalizePlaceIds(List<ScorePlaceInput> places) {
        LinkedHashSet<Long> set = new LinkedHashSet<>();
        for (ScorePlaceInput p : places) {
            if (p != null && p.placeId() != null) set.add(p.placeId());
        }
        return new ArrayList<>(set);
    }

    private static int defaultTopK(ScoreOptions options) {
        int v = options.topK();
        if (v <= 0) return 20;
        return Math.min(v, 100);
    }

    private static FeatureWindow defaultWindow(ScoreOptions options) {
        return options.window() != null ? options.window() : FeatureWindow.H24;
    }
}
