package com.dateguide.auth.support.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RefreshTokenInvalidException extends ResponseStatusException {
    public RefreshTokenInvalidException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }
}
