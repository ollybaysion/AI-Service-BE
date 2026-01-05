package com.dateguide.scoring.api;

import com.dateguide.scoring.api.dto.ScoreRequest;
import com.dateguide.scoring.api.dto.ScoreResponse;
import com.dateguide.scoring.application.ScoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class ScoreInternalController {

    private final ScoreService scoreService;

    public ScoreInternalController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/score")
    public ResponseEntity<ScoreResponse> score(@Valid @RequestBody ScoreRequest request) {
        ScoreResponse response = scoreService.score(request);
        return ResponseEntity.ok(response);
    }
}
