package com.dateguide.auth.adapter.in.oauth;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;

        Object resp = attributes.get("response");
        if (!(resp instanceof Map<?, ?>)) throw new IllegalArgumentException("Naver attributes missing 'response'");
        this.response = (Map<String, Object>) resp;
    }

    @Override
    public String provider() {
        return "naver";
    }

    @Override
    public String providerUserId() {
        return getString(response, "id");
    }

    @Override
    public String email() {
        return getStringOrNull(response, "email");
    }

    @Override
    public String name() {
        return getStringOrNull(response, "name");
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
