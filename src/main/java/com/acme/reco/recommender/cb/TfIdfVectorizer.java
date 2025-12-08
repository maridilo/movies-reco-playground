package com.acme.reco.recommender.cb;

import java.util.*;

public class TfIdfVectorizer {
    private final Map<String,Integer> vocab = new HashMap<>();
    private final Map<Integer,Double> idf = new HashMap<>();
    private boolean fitted = false;

    public void fit(List<List<String>> documents) {
        vocab.clear(); idf.clear();
        int idx = 0;
        for (var doc : documents) for (var tok : new HashSet<>(doc))
            if (!vocab.containsKey(tok)) vocab.put(tok, idx++);
        int N = documents.size();
        int[] df = new int[vocab.size()];
        for (var doc : documents) for (var tok : new HashSet<>(doc)) df[vocab.get(tok)]++;
        for (int i=0;i<df.length;i++) idf.put(i, Math.log((N + 1.0) / (df[i] + 1.0)) + 1.0);
        fitted = true;
    }

    public Map<Integer,Double> transform(List<String> tokens) {
        if (!fitted) throw new IllegalStateException("Vectorizer not fitted");
        Map<Integer,Double> tf = new HashMap<>();
        for (var tok : tokens) {
            Integer i = vocab.get(tok);
            if (i != null) tf.merge(i, 1.0, Double::sum);
        }
        double len = tf.values().stream().mapToDouble(d->d).sum();
        Map<Integer,Double> tfidf = new HashMap<>();
        for (var e : tf.entrySet()) {
            double tfn = e.getValue() / Math.max(1.0, len);
            tfidf.put(e.getKey(), tfn * idf.get(e.getKey()));
        }
        return normalize(tfidf);
    }

    public static double cosine(Map<Integer,Double> a, Map<Integer,Double> b) {
        double dot = 0.0;
        if (a.size() < b.size()) for (var e : a.entrySet()) dot += e.getValue() * b.getOrDefault(e.getKey(), 0.0);
        else for (var e : b.entrySet()) dot += e.getValue() * a.getOrDefault(e.getKey(), 0.0);
        return dot; // ya normalizados
    }

    public static Map<Integer,Double> normalize(Map<Integer,Double> v) {
        double norm = Math.sqrt(v.values().stream().mapToDouble(x->x*x).sum());
        if (norm == 0) return v;
        Map<Integer,Double> out = new HashMap<>();
        for (var e : v.entrySet()) out.put(e.getKey(), e.getValue()/norm);
        return out;
    }
}
