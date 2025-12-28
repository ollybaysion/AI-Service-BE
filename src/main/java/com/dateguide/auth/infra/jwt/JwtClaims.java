package com.dateguide.auth.infra.jwt;

import com.dateguide.auth.domain.model.UserRole;

import java.time.Instant;

public record JwtClaims(
        long userId,
        UserRole role,
        Instant issuedAt,
        Instant expiresAt,
        String issuer
) {}
