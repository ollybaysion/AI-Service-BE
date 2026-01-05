package com.dateguide.scoring.domain.port;

import com.dateguide.scoring.api.dto.FeatureWindow;
import com.dateguide.scoring.domain.port.model.PlaceFeatures;

import java.util.List;
import java.util.Map;

public interface FeatureProvider {

    FeatureFetchResult getFeatures(List<Long> placeIds, FeatureWindow window);

    record FeatureFetchResult(
            Map<Long, PlaceFeatures> features,
            CacheStatus cacheStatus
    ) {}

    enum CacheStatus {
        HIT, MISS, PARTIAL
    }
}
