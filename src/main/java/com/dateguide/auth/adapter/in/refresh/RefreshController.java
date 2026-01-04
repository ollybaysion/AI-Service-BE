package com.dateguide.auth.adapter.in.refresh;

import com.dateguide.auth.adapter.application.refresh.RefreshService;
import com.dateguide.auth.support.exception.RefreshTokenMissingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class RefreshController {

    private final RefreshService refreshService;

    public RefreshController(RefreshService refreshService) {
        this.refreshService = refreshService;
    }

    public ResponseEntity<RefreshResponse> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RefreshTokenMissingException();
        }

        return ResponseEntity.ok(this.refreshService.refresh(refreshToken));
    }
}
