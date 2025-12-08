package com.acme.reco.api.dto;

import java.util.*;

public record SimilarResponseDTO(UUID itemId, List<RecItemDTO> items) {}
