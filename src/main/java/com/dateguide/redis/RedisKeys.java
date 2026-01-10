package com.dateguide.redis;

public final class RedisKeys {
    private RedisKeys() {}

    private static final String PREFIX = "FeatureProvider";
    private static final String VERSION = "v1";

    public static String placeFeatures(long placeId) {
        return PREFIX + ":place:" + placeId + ":" + VERSION;
    }
}
