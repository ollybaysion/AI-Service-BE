package com.dateguide.auth.adapter.in.oauth;

import java.util.Map;

public final class OAuth2UserInfoFactory {

    public OAuth2UserInfoFactory() {
    }

    public static OAuth2UserInfo from(String registrationId, Map<String, Object> attributes) {
        String id = registrationId.toLowerCase();

        return switch (id) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "naver" -> new NaverOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        };
    }
}
