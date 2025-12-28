package com.dateguide.auth.adapter.in.security;

import com.dateguide.auth.domain.model.UserRole;
import com.dateguide.auth.infra.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtProvider jwtProvider;

    @Test
    void protected_endpoint_without_token_should_401() throws Exception {
        mockMvc.perform(get("/api/v1/secure/ping"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protected_endpoint_with_valid_token_should_200() throws Exception {
        String token = jwtProvider.createAccessToken(123L, UserRole.USER);

        mockMvc.perform(get("/api/v1/secure/ping")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("pong userId=123"));
    }

    @Test
    void protected_endpoint_with_valid_token_with_cookie_should_200() throws Exception {
        String token = jwtProvider.createAccessToken(123L, UserRole.USER);

        mockMvc.perform(get("/api/v1/secure/ping")
                        .cookie(new Cookie("access_token", token)))
                .andExpect(status().isOk())
                .andExpect(content().string("pong userId=123"));
    }

    @Test
    void protected_endpoint_with_invalid_token_should_401() throws Exception {
        mockMvc.perform(get("/api/v1/secure/ping")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void permitAll_endpoint_without_token_should_200() throws Exception {
        mockMvc.perform(get("/api/v1/auth/public-ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("public pong"));
    }
}