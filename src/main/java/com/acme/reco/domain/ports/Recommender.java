package com.acme.reco.domain.ports;

import com.acme.reco.domain.model.*;
import java.util.*;

public interface Recommender {
    List<Recommendation> topN(UUID userId, int k, Optional<Filter> filter);
    List<Recommendation> similarItems(UUID itemId, int k);
    Explanation explain(UUID userId, UUID itemId);
}
