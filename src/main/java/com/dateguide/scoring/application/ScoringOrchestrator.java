package com.dateguide.scoring.application;

import com.dateguide.scoring.api.dto.ScoreRequest;
import com.dateguide.scoring.api.dto.ScoreResponse;

public interface ScoringOrchestrator {
    ScoreResponse orchestrate(ScoreRequest request);
}
