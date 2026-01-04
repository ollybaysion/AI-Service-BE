package com.dateguide.auth.adapter.application.token;

import com.dateguide.auth.domain.model.UserRole;
import com.dateguide.auth.infra.jwt.JwtClaims;

public interface TokenService {

    JwtClaims validateAndParse(String jwt);

    String createAccessToken(Long userId, UserRole userRole);

    long accessTtlSeconds();
}
