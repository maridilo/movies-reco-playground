package com.acme.reco.recommender.cb;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public final class TextUtils {
    private static final Set<String> STOP = Set.of(
            "el","la","los","las","un","una","de","del","y","o","a","en","con","por","para","se","que","es","al","lo");

    public static List<String> tokenize(String text) {
        if (text == null) return List.of();
        String t = Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9\\s]", " ");
        return Arrays.stream(t.split("\\s+"))
                .filter(s -> s.length() > 2 && !STOP.contains(s))
                .collect(Collectors.toList());
    }

    public static List<String> bagOfWords(String title, String overview, List<String> genres, List<String> tags) {
        var tokens = new ArrayList<String>();
        tokens.addAll(tokenize(title));
        tokens.addAll(tokenize(overview));
        if (genres != null) tokens.addAll(genres.stream().map(g -> "genre_"+g.toLowerCase()).toList());
        if (tags != null) tokens.addAll(tags.stream().map(t -> "tag_"+t.toLowerCase()).toList());
        return tokens;
    }
}
