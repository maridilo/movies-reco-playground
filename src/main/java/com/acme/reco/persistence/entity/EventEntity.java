package com.acme.reco.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "admin_id", nullable = false, columnDefinition = "uuid")
    private UUID adminId;

    @Column(name = "movie_id", columnDefinition = "uuid")
    private UUID movieId;

    @Column(nullable = false, length = 40)
    private String type; // ADD, UPDATE, DELETE, etc.

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant ts;

    @Column(length = 4000)
    private String detail;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAdminId() { return adminId; }
    public void setAdminId(UUID adminId) { this.adminId = adminId; }
    public UUID getMovieId() { return movieId; }
    public void setMovieId(UUID movieId) { this.movieId = movieId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Instant getTs() { return ts; }
    public void setTs(Instant ts) { this.ts = ts; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}