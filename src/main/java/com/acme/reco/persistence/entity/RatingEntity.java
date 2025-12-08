package com.acme.reco.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ratings")
public class RatingEntity {

    @EmbeddedId
    private RatingId id;

    @Column(nullable = false)
    private int score;

    @Column(length = 2000)
    private String comment;

    @Column(name = "ts", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant ts;

    public RatingEntity() {}

    // getters/setters
    public RatingId getId() { return id; }
    public void setId(RatingId id) { this.id = id; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Instant getTs() { return ts; }
    public void setTs(Instant ts) { this.ts = ts; }
}
