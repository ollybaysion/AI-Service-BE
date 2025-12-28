package com.dateguide.auth.adapter.in.oauth;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String provider() {
        return "google";
    }

    @Override
    public String providerUserId() {
        return getString(attributes, "sub");
    }

    @Override
    public String email() {
        return getStringOrNull(attributes, "email");
    }

    @Override
    public String name() {
        return getStringOrNull(attributes, "name");
    }

    private static String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) throw new IllegalArgumentException("Missing required attributes: " + key);
        return String.valueOf(v);
    }

    private static String getStringOrNull(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? null : String.valueOf(v);
    }
}
