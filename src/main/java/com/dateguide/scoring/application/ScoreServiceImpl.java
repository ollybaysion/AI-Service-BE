package com.dateguide.scoring.application;

import com.dateguide.scoring.api.dto.ScoreRequest;
import com.dateguide.scoring.api.dto.ScoreResponse;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl implements ScoreService{

    private final ScoringOrchestrator orchestrator;

    public ScoreServiceImpl(ScoringOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public ScoreResponse score(ScoreRequest request) {
        return orchestrator.orchestrate(request);
    }
}
