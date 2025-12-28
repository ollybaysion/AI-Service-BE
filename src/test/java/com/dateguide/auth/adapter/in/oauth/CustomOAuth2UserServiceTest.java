package com.dateguide.auth.adapter.in.oauth;

import com.dateguide.auth.adapter.out.persistence.UserRepository;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.OAuthProvider;
import com.dateguide.auth.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DataJpaTest
class CustomOAuth2UserServiceTest {

    @Autowired
    UserRepository userRepository;

    CustomOAuth2UserService service;

    RestTemplate restTemplate;
    MockRestServiceServer server;

    @BeforeEach
    void setup() {
        service = new CustomOAuth2UserService(userRepository);

        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);

        service.setRestOperations(restTemplate);
    }

    @Test
    void google_first_login_creates_user_and_returns_oauth2user_with_userId() {
        // given
        String userInfoUri = "https://openidconnect.googleapis.com/v1/userinfo";
        String tokenValue = "google-token";

        server.expect(once(), requestTo(userInfoUri))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + tokenValue))
                .andRespond(withSuccess("""
                        {
                          "sub": "g-sub-123",
                          "email": "a@gmail.com",
                          "name": "Alice"
                        }
                        """, MediaType.APPLICATION_JSON));

        OAuth2UserRequest userRequest = userRequest("google", userInfoUri, "sub", tokenValue);

        // when
        OAuth2User principal = service.loadUser(userRequest);
        server.verify();

        // then
        // DB
        UserEntity saved = userRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "g-sub-123")
                .orElseThrow();

        assertThat(saved.getEmail()).isEqualTo("a@gmail.com");
        assertThat(saved.getName()).isEqualTo("Alice");
        assertThat(saved.getRole()).isEqualTo(UserRole.USER);

        // principal
        assertThat((Long) principal.getAttribute("userId")).isEqualTo(saved.getId());
        assertThat(principal.getName()).isEqualTo(Long.toString(saved.getId()));
        assertThat((String) principal.getAttribute("role")).isEqualTo(UserRole.USER.name());
    }

    @Test
    void naver_first_login_creates_user_and_returns_oauth2user_with_userId() {
        // given
        String userInfoUri = "https://openapi.naver.com/v1/nid/me";
        String tokenValue = "naver-token";

        server.expect(once(), requestTo(userInfoUri))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + tokenValue))
                .andRespond(withSuccess("""
                        {
                          "resultcode": "00",
                          "message": "success",
                          "response": {
                            "id": "n-id-777",
                            "email": "n@naver.com",
                            "name": "홍길동"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        OAuth2UserRequest userRequest = userRequest("naver", userInfoUri, "response", tokenValue);

        // when
        OAuth2User principal = service.loadUser(userRequest);
        server.verify();

        // then
        UserEntity saved = userRepository.findByProviderAndProviderUserId(OAuthProvider.NAVER, "n-id-777")
                .orElseThrow();

        assertThat(saved.getEmail()).isEqualTo("n@naver.com");
        assertThat(saved.getName()).isEqualTo("홍길동");
        assertThat(saved.getRole()).isEqualTo(UserRole.USER);

        // principal
        assertThat((Long) principal.getAttribute("userId")).isEqualTo(saved.getId());
        assertThat(principal.getName()).isEqualTo(Long.toString(saved.getId()));
        assertThat((String) principal.getAttribute("role")).isEqualTo(UserRole.USER.name());
    }

    @Test
    void google_second_login_updated_existing_user_not_create_new() {
        // given
        UserEntity existing = new UserEntity(OAuthProvider.GOOGLE, "g-sub-123", "old@gmail.com", "OldName");
        userRepository.saveAndFlush(existing);

        long beforeCount = userRepository.count();

        String userInfoUri = "https://openidconnect.googleapis.com/v1/userinfo";
        String tokenValue = "google-token-2";

        server.expect(once(), requestTo(userInfoUri))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + tokenValue))
                .andRespond(withSuccess("""
                        {
                          "sub": "g-sub-123",
                          "email": "new@gmail.com",
                          "name": "NewName"
                        }
                        """, MediaType.APPLICATION_JSON));

        OAuth2UserRequest userRequest = userRequest("google", userInfoUri, "sub", tokenValue);

        // when
        OAuth2User principal = service.loadUser(userRequest);
        server.verify();

        // then
        assertThat(userRepository.count()).isEqualTo(beforeCount);

        UserEntity updated = userRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "g-sub-123")
                .orElseThrow();

        assertThat(updated.getId()).isEqualTo(existing.getId());
        assertThat(updated.getEmail()).isEqualTo("new@gmail.com");
        assertThat(updated.getName()).isEqualTo("NewName");
        assertThat((Long) principal.getAttribute("userId")).isEqualTo(existing.getId());
    }

    private OAuth2UserRequest userRequest(
            String registrationId,
            String userInfoUri,
            String userNameAttributeName,
            String accessTokenValue
    ) {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId(registrationId)
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/loin/oauth2/code/" + registrationId)
                .authorizationUri("http://localhost/oauth2/authorize")
                .tokenUri("http://localhost/oauth2/token")
                .userInfoUri(userInfoUri)
                .userNameAttributeName(userNameAttributeName)
                .scope("profile", "email")
                .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                accessTokenValue,
                Instant.now(),
                Instant.now().plusSeconds(60)
        );

        return new OAuth2UserRequest(registration, token);
    }
}