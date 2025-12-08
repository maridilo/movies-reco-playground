package com.acme.reco.persistence.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RatingId implements Serializable {
    private UUID movieId;
    private UUID userId;

    public RatingId() {}

    public RatingId(UUID movieId, UUID userId) {
        this.movieId = movieId;
        this.userId = userId;
    }

    public UUID getMovieId() { return movieId; }
    public void setMovieId(UUID movieId) { this.movieId = movieId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RatingId)) return false;
        RatingId that = (RatingId) o;
        return Objects.equals(movieId, that.movieId) &&
                Objects.equals(userId, that.userId);
    }
    @Override public int hashCode() {
        return Objects.hash(movieId, userId);
    }
}
