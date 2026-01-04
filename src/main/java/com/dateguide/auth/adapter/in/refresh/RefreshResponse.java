package com.dateguide.auth.adapter.in.refresh;

public record RefreshResponse(
        String accessToken,
        long expiresInSeconds
) {}
