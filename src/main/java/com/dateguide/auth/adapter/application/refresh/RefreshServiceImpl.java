package com.dateguide.auth.adapter.application.refresh;

import com.dateguide.auth.adapter.application.token.TokenService;
import com.dateguide.auth.adapter.in.refresh.RefreshResponse;
import com.dateguide.auth.adapter.out.persistence.RefreshTokenRepository;
import com.dateguide.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.dateguide.auth.domain.model.UserRole;
import com.dateguide.auth.infra.jwt.JwtClaims;
import com.dateguide.auth.support.exception.RefreshTokenExpiredException;
import com.dateguide.auth.support.exception.RefreshTokenInvalidException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RefreshServiceImpl implements RefreshService {

    private final TokenService tokenService;
    private final TokenHashService tokenHashService;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshServiceImpl(TokenService tokenService, TokenHashService tokenHashService, RefreshTokenRepository refreshTokenRepository) {
        this.tokenService = tokenService;
        this.tokenHashService = tokenHashService;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public RefreshResponse refresh(String refreshToken) {

        final JwtClaims claims;
        try {
            claims = tokenService.validateAndParse(refreshToken);
        } catch (Exception e) {
            throw new RefreshTokenInvalidException();
        }

        String tokenHash = tokenHashService.hmacSha256Base64Url(refreshToken);

        RefreshTokenEntity entity = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(RefreshTokenInvalidException::new);

        if (entity.getRevokedAt() != null) {
            throw new RefreshTokenInvalidException();
        }
        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(Instant.now())) {
            throw new RefreshTokenExpiredException();
        }

        Long userId = claims.userId();
        UserRole role = claims.role();

        String newAccessToken = tokenService.createAccessToken(userId, role);

        return new RefreshResponse(newAccessToken, tokenService.accessTtlSeconds());
    }
}
