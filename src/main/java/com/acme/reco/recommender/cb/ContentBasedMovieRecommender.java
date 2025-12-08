package com.acme.reco.recommender.cb;

import com.acme.reco.domain.model.*;
import com.acme.reco.domain.ports.Recommender;
import com.acme.reco.domain.ports.PopularityProvider;

import java.util.*;
import java.util.stream.Collectors;

/** Content-based para películas: TF-IDF + blending con popularidad. */
public class ContentBasedMovieRecommender implements Recommender {
    private final Map<UUID, Movie> catalog;
    private final Map<UUID, Map<Integer,Double>> movieVectors;
    private final TfIdfVectorizer vectorizer = new TfIdfVectorizer();
    private final Map<UUID, Map<Integer,Double>> userProfiles = new HashMap<>();

    private PopularityProvider popularity; // opcional
    private double alpha = 0.7; // peso del CB en el blending (0..1)

    public ContentBasedMovieRecommender(List<Movie> movies) {
        this.catalog = movies.stream().collect(Collectors.toMap(Movie::id, m -> m));
        var docs = movies.stream()
                .map(m -> TextUtils.bagOfWords(m.title(), m.overview(), m.genres(), m.tags()))
                .toList();
        vectorizer.fit(docs);
        this.movieVectors = new HashMap<>();
        for (Movie m : movies) {
            movieVectors.put(m.id(), vectorizer.transform(
                    TextUtils.bagOfWords(m.title(), m.overview(), m.genres(), m.tags())));
        }
    }

    public void setUserProfile(UUID userId, List<UUID> likedMovies) {
        Map<Integer,Double> acc = new HashMap<>();
        for (UUID mid : likedMovies) {
            var v = movieVectors.get(mid);
            if (v == null) continue;
            for (var e : v.entrySet()) acc.merge(e.getKey(), e.getValue(), Double::sum);
        }
        userProfiles.put(userId, TfIdfVectorizer.normalize(acc));
    }

    public void setPopularityProvider(PopularityProvider provider) { this.popularity = provider; }
    public void setAlpha(double alpha) { this.alpha = Math.max(0.0, Math.min(1.0, alpha)); }

    @Override
    public List<Recommendation> topN(UUID userId, int k, Optional<Filter> filter) {
        var u = userProfiles.get(userId);

        // Si no hay perfil: usa popularidad (cold-start)
        if (u == null || u.isEmpty()) {
            return catalog.values().stream()
                    .filter(m -> passes(m, filter))
                    .map(m -> new Recommendation(m.id(), pop(m.id()), List.of("popularidad")))
                    .sorted(Comparator.comparingDouble(Recommendation::score).reversed())
                    .limit(k)
                    .toList();
        }

        // CB puro + blending con popularidad
        return catalog.values().stream()
                .filter(m -> passes(m, filter))
                .map(m -> {
                    double cb = TfIdfVectorizer.cosine(u, movieVectors.get(m.id())); // 0..1
                    double blended = alpha * cb + (1 - alpha) * pop(m.id());
                    return new Recommendation(m.id(), blended, List.of("cb:%.2f".formatted(cb), "pop:%.2f".formatted(pop(m.id()))));
                })
                .sorted(Comparator.comparingDouble(Recommendation::score).reversed())
                .limit(k)
                .toList();
    }

    @Override
    public List<Recommendation> similarItems(UUID movieId, int k) {
        var v = movieVectors.get(movieId);
        if (v == null) return List.of();
        return catalog.values().stream()
                .filter(m -> !m.id().equals(movieId))
                .map(m -> new Recommendation(m.id(), TfIdfVectorizer.cosine(v, movieVectors.get(m.id())), List.of("similar a "+movieId)))
                .sorted(Comparator.comparingDouble(Recommendation::score).reversed())
                .limit(k)
                .toList();
    }

    @Override
    public Explanation explain(UUID userId, UUID itemId) {
        return new Explanation("Blending CB (%.0f%%) + Popularidad (%.0f%%)".formatted(alpha*100, (1-alpha)*100));
    }

    private boolean passes(Movie m, Optional<Filter> f) {
        if (f.isEmpty()) return true;
        var F = f.get();
        boolean g = (F.genre()==null) || (m.genres()!=null && m.genres().stream().anyMatch(x -> x.equalsIgnoreCase(F.genre())));
        boolean y = (F.minYear()==null) || (m.year()!=null && m.year() >= F.minYear());
        return g && y;
    }

    private double pop(UUID id) { return popularity == null ? 0.0 : popularity.score(id); }
}
