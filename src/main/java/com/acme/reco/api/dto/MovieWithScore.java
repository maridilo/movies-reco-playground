package com.acme.reco.api.dto;

import com.acme.reco.persistence.entity.MovieEntity;

public record MovieWithScore(MovieEntity movie, double score) {}
