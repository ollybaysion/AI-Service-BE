package com.dateguide.auth.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.cookie")
public record AuthCookieProperties(
        String domain,
        String path,
        boolean secure,
        String sameSite,
        int accessMaxAgeSeconds,
        int refreshMaxAgeSeconds,
        String accessCookieName,
        String refreshCookieName,
        String redirectUrl
) {}
