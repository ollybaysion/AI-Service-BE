package com.dateguide.scoring.domain.port.model;

import com.dateguide.redis.RedisFields;
import com.dateguide.redis.RedisKeys;
import com.dateguide.redis.RedisUtil;
import com.dateguide.scoring.api.dto.FeatureWindow;
import com.dateguide.scoring.domain.port.FeatureProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class FeatureProviderV1 implements FeatureProvider {

    private static final Duration DEFAULT_TTL = Duration.ofDays(14);

    private final RedisUtil redis;

    public FeatureProviderV1(RedisUtil redis) {
        this.redis = redis;
    }

    @Override
    public FeatureFetchResult getFeatures(List<Long> placeIds, FeatureWindow window) {
        Map<Long, PlaceFeatures> result = new HashMap<>(placeIds.size());

        int hit = 0;
        int miss = 0;

        for (Long placeIdObj : placeIds) {
            long placeId = placeIdObj;
            String key = RedisKeys.placeFeatures(placeId);

            List<String> values = redis.hmgetAsList(
                    key,
                    RedisFields.QUALITY_SCORE,
                    RedisFields.UPDATED_AT
            );

            String qualityStr = values.get(0);

            if (qualityStr == null) {
                result.put(placeId, PlaceFeatures.missing(placeId));
                miss++;
                continue;
            }

            double qualityScore;
            try {
                qualityScore = Double.parseDouble(qualityStr);
            } catch (NumberFormatException e) {
                result.put(placeId, PlaceFeatures.missing(placeId));
                miss++;
                continue;
            }

            PlaceFeatures features = new PlaceFeatures(
                    placeId,
                    false,
                    0.0,
                    qualityScore,
                    0.0,
                    0.0
            );

            result.put(placeId, features);
            hit++;
        }

        CacheStatus status;
        if (hit == placeIds.size()) status = CacheStatus.HIT;
        else if (hit == 0) status = CacheStatus.MISS;
        else status = CacheStatus.PARTIAL;

        return new FeatureFetchResult(result, status);
    }

    public void putPlaceFeature(long placeId, double qualityScore, long updatedAtEpochMillis) {
        putPlaceFeature(placeId, qualityScore, updatedAtEpochMillis, DEFAULT_TTL);
    }

    public void putPlaceFeature(long placeId, double qualityScore, long updatedAtEpochMillis, Duration ttl) {
        String key = RedisKeys.placeFeatures(placeId);

        redis.hset(key, RedisFields.QUALITY_SCORE, Double.toString(qualityScore));
        redis.hset(key, RedisFields.UPDATED_AT, Long.toString(updatedAtEpochMillis));

        redis.expire(key, ttl);
    }
}
