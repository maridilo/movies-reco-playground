package com.acme.reco.api.dto;

import java.util.*;

public record RecItemDTO(UUID itemId, double score, List<String> reasons) {}
