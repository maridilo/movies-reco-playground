package com.acme.reco.api.dto;

import java.util.UUID;

public record TopMovieStat(UUID movieId, String title, double avg, long count) {}