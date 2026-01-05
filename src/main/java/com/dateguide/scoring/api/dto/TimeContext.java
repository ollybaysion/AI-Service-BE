package com.dateguide.scoring.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TimeContext(
        @NotNull LocalDateTime now
) {}
