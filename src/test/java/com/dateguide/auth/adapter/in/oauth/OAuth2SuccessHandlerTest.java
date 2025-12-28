package com.dateguide.auth.adapter.in.oauth;

import com.dateguide.auth.adapter.out.persistence.RefreshTokenRepository;
import com.dateguide.auth.adapter.out.persistence.UserRepository;
import com.dateguide.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.UserRole;
import com.dateguide.auth.infra.AuthCookieProperties;
import com.dateguide.auth.infra.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

class OAuth2SuccessHandlerTest {

    JwtProvider jwtProvider = mock(JwtProvider.class);
    UserRepository userRepository = mock(UserRepository.class);
    RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);

    AuthCookieProperties cookieProps;
    OAuth2SuccessHandler handler;

    @BeforeEach
    void setUp() {
        cookieProps = new AuthCookieProperties(
                "",
                "/",
                false,
                "Lax",
                900,
                2592000,
                "access_token",
                "refresh_token",
                "http://localhost:3000/auth/callback"
        );
        handler = new OAuth2SuccessHandler(jwtProvider, userRepository, refreshTokenRepository, cookieProps);
    }

    @Test
    void success_sets_httpOnly_cookies_and_redirects() throws Exception {
        // given
        Long userId = 123L;

        UserEntity user = new UserEntity(null, "x", "a@a.com", "Alice");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtProvider.createAccessToken(userId, UserRole.USER)).thenReturn("ACCESS.JWT.VALUE");

        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OAuth2User principal = new DefaultOAuth2User(
                List.of(() -> "ROLE_USER"),
                Map.of("userId", userId),
                "userId"
        );

        var authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        // when
        handler.onAuthenticationSuccess(req, res, authentication);

        // then: redirect
        assertThat(res.getStatus()).isEqualTo(302);
        assertThat(res.getRedirectedUrl()).isEqualTo(cookieProps.redirectUrl());

        // then: Set-Cookie
        List<String> setCookies = res.getHeaders(SET_COOKIE);
        assertThat(setCookies).hasSize(2);

        String accessCookie = setCookies.stream().filter(c -> c.startsWith("access_token=")).findFirst().orElseThrow();
        String refreshCookie = setCookies.stream().filter(c -> c.startsWith("refresh_token=")).findFirst().orElseThrow();

        // access cookie
        assertThat(accessCookie).contains("access_token=ACCESS.JWT.VALUE");
        assertThat(accessCookie).contains("Path=/");
        assertThat(accessCookie).contains("HttpOnly");
        assertThat(accessCookie).contains("Max-Age=900");
        assertThat(accessCookie).contains("SameSite=Lax");

        // refresh cookie
        assertThat(refreshCookie).contains("refresh_token=");
        assertThat(refreshCookie).contains("Path=/");
        assertThat(refreshCookie).contains("HttpOnly");
        assertThat(refreshCookie).contains("Max-Age=2592000");
        assertThat(refreshCookie).contains("SameSite=Lax");

        // then: refreshToken DB 저장 호출 확인
        ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(refreshTokenRepository, times(1)).save(captor.capture());

        RefreshTokenEntity saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getTokenHash()).isNotBlank();
        assertThat(saved.getExpiresAt()).isNotNull();

        verify(jwtProvider, times(1)).createAccessToken(userId, UserRole.USER);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void missing_userId_returns_401() throws Exception {
        // given
        OAuth2User principal = new DefaultOAuth2User(
                List.of(() -> "ROLE_USER"),
                Map.of("key", "value"),
                "key"
        );
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        // when
        handler.onAuthenticationSuccess(req, res, authentication);

        // then
        assertThat(res.getStatus()).isEqualTo(401);
        verifyNoInteractions(jwtProvider, userRepository, refreshTokenRepository);
    }
}