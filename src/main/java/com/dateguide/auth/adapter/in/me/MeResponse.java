package com.dateguide.auth.adapter.in.me;

public record MeResponse(
        Long userId,
        String email,
        String name
) { }
