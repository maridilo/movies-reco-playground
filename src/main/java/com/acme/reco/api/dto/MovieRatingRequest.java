package com.acme.reco.api.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record MovieRatingRequest(
        @NotNull UUID userId,
        @Min(1) @Max(5) int score,
        @Size(max = 2000) String comment
) {}
