package com.acme.reco.api.dto;

import java.util.*;

public record RecResponseDTO(UUID userId, String modelVersion, List<RecItemDTO> items) {}
