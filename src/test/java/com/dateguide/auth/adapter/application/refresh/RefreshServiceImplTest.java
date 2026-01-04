package com.dateguide.auth.adapter.application.refresh;

import com.dateguide.auth.adapter.application.token.TokenService;
import com.dateguide.auth.adapter.in.refresh.RefreshResponse;
import com.dateguide.auth.adapter.out.persistence.RefreshTokenRepository;
import com.dateguide.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.OAuthProvider;
import com.dateguide.auth.domain.model.UserRole;
import com.dateguide.auth.infra.jwt.JwtClaims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshServiceImplTest {

    @Mock TokenService tokenService;
    @Mock TokenHashService tokenHashService;
    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks RefreshServiceImpl refreshService;

    @Test
    void refresh_returnsNewAccessToken() {
        // given
        String rawRefreshToken = "raw.refresh.jwt";
        String tokenHash = "hash-value";

        Instant future = Instant.now().plusSeconds(3600);

        UserEntity user = new UserEntity(OAuthProvider.GOOGLE, "p-123", "a@gmail.com", "Alice");

        JwtClaims claims = new JwtClaims(
                1L, UserRole.USER,
                Instant.now().minusSeconds(10),
                future,
                "issuer"
        );

        RefreshTokenEntity refreshToken = new RefreshTokenEntity(user, tokenHash, future);

        when(tokenService.validateAndParse(rawRefreshToken)).thenReturn(claims);
        when(tokenHashService.hmacSha256Base64Url(rawRefreshToken)).thenReturn(tokenHash);
        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(refreshToken));

        when(tokenService.createAccessToken(1L, UserRole.USER))
                .thenReturn("new.access.jwt");
        when(tokenService.accessTtlSeconds())
                .thenReturn(900L);

        // when
        RefreshResponse res = refreshService.refresh(rawRefreshToken);

        // then
        assertNotNull(res);
        assertEquals("new.access.jwt", res.accessToken());
        assertEquals(900L, res.expiresInSeconds());
    }
}