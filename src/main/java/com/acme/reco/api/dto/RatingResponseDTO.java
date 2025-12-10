package com.acme.reco.api.dto;

import com.acme.reco.persistence.entity.RatingEntity;
import com.acme.reco.persistence.entity.RatingId;

import java.util.UUID;

public class RatingResponseDTO {

    private UUID userId;
    private UUID movieId;
    private int score;
    private String comment;

    public static RatingResponseDTO fromEntity(RatingEntity r) {
        RatingResponseDTO dto = new RatingResponseDTO();
        RatingId id = r.getId();
        dto.setUserId(id.getUserId());
        dto.setMovieId(id.getMovieId());
        dto.setScore(r.getScore());
        dto.setComment(r.getComment());
        return dto;
    }

    // getters y setters

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getMovieId() {
        return movieId;
    }

    public void setMovieId(UUID movieId) {
        this.movieId = movieId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
