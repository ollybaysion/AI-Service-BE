package com.dateguide.scoring.domain.port;

import com.dateguide.scoring.api.dto.TimeContext;
import com.dateguide.scoring.api.dto.UserContext;
import com.dateguide.scoring.domain.port.model.PlaceFeatures;
import com.dateguide.scoring.domain.port.model.ScoreOutput;

public interface ScoreCalculator {
    ScoreOutput score(long placeId, PlaceFeatures f, UserContext user, TimeContext time);
}
