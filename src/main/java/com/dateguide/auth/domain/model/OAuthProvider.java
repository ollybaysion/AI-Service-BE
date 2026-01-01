package com.dateguide.auth.domain.model;

public enum OAuthProvider {
    GOOGLE, NAVER;

    public static OAuthProvider from(String provider) {
        if (provider.equals("google")) return GOOGLE;
        else if (provider.equals("naver")) return NAVER;

        return null;
    }
}
