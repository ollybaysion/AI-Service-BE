package com.dateguide.auth.adapter.in.oauth;

import com.dateguide.auth.adapter.out.persistence.RefreshTokenRepository;
import com.dateguide.auth.adapter.out.persistence.UserRepository;
import com.dateguide.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.OAuthProvider;
import com.dateguide.auth.domain.model.UserRole;
import com.dateguide.auth.infra.AuthCookieProperties;
import com.dateguide.auth.infra.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthCookieProperties cookieProps;

    public OAuth2SuccessHandler(JwtProvider jwtProvider, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, AuthCookieProperties cookieProps) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.cookieProps = cookieProps;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OidcUser principal = (OidcUser) authentication.getPrincipal();

        OAuthProvider provider = OAuthProvider.from(token.getAuthorizedClientRegistrationId().toLowerCase());
        String providerId = principal.getSubject();

        UserEntity user = userRepository.findByProviderAndProviderUserId(provider, providerId)
                .orElseThrow(() -> new IllegalStateException("User not found" + provider + providerId));

        UserRole role = user.getRole();

        String accessToken = jwtProvider.createAccessToken(user.getId(), role);

        String refreshToken = UUID.randomUUID().toString();
        Instant refreshExpiresAt = Instant.now().plusSeconds(cookieProps.refreshMaxAgeSeconds());

        RefreshTokenEntity rt = new RefreshTokenEntity(user, refreshToken, refreshExpiresAt);
        refreshTokenRepository.save(rt);

        ResponseCookie accessCookie = buildCookie(
                cookieProps.accessCookieName(),
                accessToken,
                cookieProps.accessMaxAgeSeconds()
        );

        ResponseCookie refreshCookie = buildCookie(
                cookieProps.refreshCookieName(),
                refreshToken,
                cookieProps.refreshMaxAgeSeconds()
        );

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.sendRedirect(cookieProps.redirectUrl());
    }

    private ResponseCookie buildCookie(String name, String value, int maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProps.secure())
                .path(cookieProps.path())
                .maxAge(maxAgeSeconds);

        if (cookieProps.domain() != null && !cookieProps.domain().isBlank()) {
            b.domain(cookieProps.domain());
        }

        b.sameSite(cookieProps.sameSite());

        return b.build();
    }
}
