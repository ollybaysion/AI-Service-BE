package com.dateguide.scoring.domain.port.model;

import com.dateguide.scoring.api.dto.FeatureWindow;
import com.dateguide.scoring.domain.port.FeatureProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class FeatureProviderDummy implements FeatureProvider {

    @Override
    public FeatureFetchResult getFeatures(List<Long> placeIds, FeatureWindow window) {
        Map<Long, PlaceFeatures> out = new HashMap<>();

        for (Long id : placeIds) {
            if (id == null) continue;

            double base = pseudo01(id);

            double trend = switch (window) {
                case H1 -> clamp01(base * 0.9 + 0.05);
                case H24 -> clamp01(base);
                case D7 -> clamp01(base * 0.8 + 0.10);
            };

            double quality = clamp01(0.3 + 0.7 * pseudo01(id * 31));
            double volatility = clamp01(0.2 * pseudo01(id * 97));
            double freshness = clamp01(0.5 + 0.5 * pseudo01(id * 7));

            out.put(id, new PlaceFeatures(id, false, trend, quality, volatility, freshness));
        }

        return new FeatureFetchResult(out, CacheStatus.HIT);
    }

    private static double pseudo01(long seed) {
        long x = seed ^ (seed << 13);
        x ^= (x >>> 7);
        x ^= (x << 17);
        long positive = x & 0x7fffffffL;
        return (positive % 10_000) / 10_000.0;
    }


    private static double clamp01(double v) {
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }
}
