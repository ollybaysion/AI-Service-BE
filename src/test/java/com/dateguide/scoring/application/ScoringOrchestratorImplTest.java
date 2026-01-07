package com.dateguide.scoring.application;

import com.dateguide.scoring.api.dto.*;
import com.dateguide.scoring.domain.port.FeatureProvider;
import com.dateguide.scoring.domain.port.ScoreCalculator;
import com.dateguide.scoring.domain.port.model.PlaceFeatures;
import com.dateguide.scoring.domain.port.model.ScoreOutput;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ScoringOrchestratorImplTest {

    @Test
    void orchestrate_sorts_desc_and_applied_topk_and_dedup() {
        FeatureProvider fp = (placeIds, window) -> new FeatureProvider.FeatureFetchResult(
                Map.of(
                        1L, new PlaceFeatures(1L, false, 0,0,0,0),
                        2L, new PlaceFeatures(2L, false, 0,0,0,0),
                        3L, new PlaceFeatures(3L, false, 0,0,0,0)
                ),
                FeatureProvider.CacheStatus.HIT
        );

        ScoreCalculator calc = (placeId, f, user, time) -> {
            double total = switch ((int) placeId) {
                case 1 -> 1.0;
                case 2 -> 3.0;
                case 3 -> 2.0;
                default -> 0.0;
            };
            return new ScoreOutput(placeId, total,
                    new ScoreBreakdown(0,0,0,0,0));
        };

        ScoringOrchestratorImpl orch = new ScoringOrchestratorImpl(fp, calc);

        ScoreRequest req = new ScoreRequest(
                new UserContext("u1", "Seongsu", LocalDate.of(2026, 1, 5), LocalTime.of(19, 0),
                        BudgetRange.MID, Transportation.SUBWAY, List.of("queit")),
                new TimeContext(LocalDateTime.of(2026, 1, 5, 18, 40)),
                List.of(new ScorePlaceInput(2L), new ScorePlaceInput(1L), new ScorePlaceInput(2L), new ScorePlaceInput(3L)),
                new ScoreOptions(FeatureWindow.H24, true, 2)
        );

        ScoreResponse res = orch.orchestrate(req);

        assertThat(res.scoredPlaces()).hasSize(2);
        assertThat(res.scoredPlaces().get(0).placeId()).isEqualTo(2L);
        assertThat(res.scoredPlaces().get(1).placeId()).isEqualTo(3L);
    }

}