package com.dateguide.auth.adapter.in.security;

import com.dateguide.auth.adapter.in.oauth.CustomOAuth2UserService;
import com.dateguide.auth.infra.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtProvider jwtProvider,
            CustomOAuth2UserService customOAuth2userService) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtProvider);

        http
                // 1) 기본 보안 설정
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2) Authorization 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/health",
                                "/error",
                                "/api/v1/auth/**",
                                "/oauth2**",
                                "/login/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 3) Exception Handling
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(
                                new RestAuthenticationEntryPoint()
                        )
                        .accessDeniedHandler(
                                new RestAccessDeniedHandler()
                        )
                )

                // 4) OAuth
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(customOAuth2userService))
                )

                // 5) JWT 필터 등록
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
