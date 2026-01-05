package com.dateguide.scoring.application;

import com.dateguide.scoring.api.dto.ScoreRequest;
import com.dateguide.scoring.api.dto.ScoreResponse;

public interface ScoreService {
    ScoreResponse score(ScoreRequest request);
}
