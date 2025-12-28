package com.dateguide.auth.adapter.in.oauth;

public interface OAuth2UserInfo {
    String provider();
    String providerUserId();
    String email();
    String name();
}
