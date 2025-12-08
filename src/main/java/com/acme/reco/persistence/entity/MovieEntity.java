package com.acme.reco.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "movies")
public class MovieEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String overview;

    private Integer releaseYear;

    @Column(name = "genres_csv")
    private String genresCsv;

    @Column(name = "tags_csv")
    private String tagsCsv;

    @Column(name="avg_rating")  private Double avgRating;

    @Column(name="ratings_cnt") private Integer ratingsCount;

    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    public String getGenresCsv() { return genresCsv; }
    public void setGenresCsv(String genresCsv) { this.genresCsv = genresCsv; }
    public String getTagsCsv() { return tagsCsv; }
    public void setTagsCsv(String tagsCsv) { this.tagsCsv = tagsCsv; }
    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
    public Integer getRatingsCount() { return ratingsCount; }
    public void setRatingsCount(Integer ratingsCount) { this.ratingsCount = ratingsCount; }
}
