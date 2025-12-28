package com.dateguide.auth.infra.jwt;

import com.dateguide.auth.domain.model.UserRole;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtProviderTest {

    @Test
    void create_and_parse_success() {
        JwtProperties props = new JwtProperties(
                "dateguide",
                "change-me-to-a-very-log-secret-at-least-32-bytes",
                900
        );
        JwtProvider jwtProvider = new JwtProvider(props);

        String token = jwtProvider.createAccessToken(123L, UserRole.USER);
        JwtClaims claims = jwtProvider.validateAndParse(token);

        assertThat(claims.userId()).isEqualTo(123L);
        assertThat(claims.role()).isEqualTo(UserRole.USER);
        assertThat(claims.issuer()).isEqualTo("dateguide");
    }

    @Test
    void invalid_signature_should_fail() {
        JwtProvider p1 = new JwtProvider(new JwtProperties(
                "dateguide",
                "change-me-to-a-very-log-secret-at-least-32-bytes",
                900
        ));
        JwtProvider p2 = new JwtProvider(new JwtProperties(
                "dateguide",
                "another-very-long-secret-at-least-32-bytes!!!!",
                900
        ));

        String token = p1.createAccessToken(1L, UserRole.USER);

        assertThatThrownBy(() -> p2.validateAndParse(token))
                .isInstanceOf(JwtException.class);
    }

}