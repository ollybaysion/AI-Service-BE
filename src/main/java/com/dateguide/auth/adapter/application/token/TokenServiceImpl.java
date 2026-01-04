package com.dateguide.auth.adapter.application.token;

import com.dateguide.auth.domain.model.UserRole;
import com.dateguide.auth.infra.jwt.JwtClaims;
import com.dateguide.auth.infra.jwt.JwtProperties;
import com.dateguide.auth.infra.jwt.JwtProvider;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService{

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    public TokenServiceImpl(JwtProvider jwtProvider, JwtProperties jwtProperties) {
        this.jwtProvider = jwtProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public JwtClaims validateAndParse(String jwt) {
        return jwtProvider.validateAndParse(jwt);
    }

    @Override
    public String createAccessToken(Long userId, UserRole userRole) {
        return jwtProvider.createAccessToken(userId, userRole);
    }

    @Override
    public long accessTtlSeconds() {
        return jwtProperties.accessTtlSeconds();
    }
}
