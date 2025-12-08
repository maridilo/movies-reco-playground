package com.acme.reco.domain.model;

import java.util.*;

public record Recommendation(
        UUID itemId,
        double score,
        List<String> reasons
) {}
