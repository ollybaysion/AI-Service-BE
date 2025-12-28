package com.dateguide.auth.adapter.in.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityTestController {

    @GetMapping("/api/v1/secure/ping")
    String securePing(Authentication authentication) {
        return "pong userId=" + authentication.getName();
    }

    @GetMapping("/api/v1/auth/public-ping")
    String publicPing() {
        return "public pong";
    }
}
