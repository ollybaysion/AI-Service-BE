package com.dateguide.auth.support.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RefreshTokenMissingException extends ResponseStatusException {
    public RefreshTokenMissingException() {
        super(HttpStatus.UNAUTHORIZED, "Refresh Token is missing");
    }
}
