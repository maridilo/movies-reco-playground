package com.acme.reco.domain.model;

import java.util.*;

public record Movie(
        UUID id,
        String title,
        Integer year,
        List<String> genres,
        List<String> tags,
        String overview
) {}
