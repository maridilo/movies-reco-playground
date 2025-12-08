// com/acme/reco/persistence/repo/RatingJpaRepository.java
package com.acme.reco.persistence.repo;

import com.acme.reco.persistence.entity.RatingEntity;
import com.acme.reco.persistence.entity.RatingId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface RatingJpaRepository extends JpaRepository<RatingEntity, RatingId> {

    // === Proyección para agregados por película (usada en StatsService/AdminStats) ===
    interface MovieAgg {
        UUID getMovieId();
        Double getAvg();
        Long getCnt();
    }

    // === Proyección para agregados por usuario (top raters en AdminStats) ===
    interface UserAgg {
        UUID getUserId();
        Double getAvg();
        Long getCnt();
    }

    // ---- Métodos existentes/que ya usa tu código ----
    boolean existsByIdMovieIdAndIdUserId(UUID movieId, UUID userId);
    List<RatingEntity> findAllByIdMovieId(UUID movieId);
    List<RatingEntity> findAllByIdUserId(UUID userId);
    List<RatingEntity> findAllByIdUserIdAndScoreGreaterThanEqual(UUID userId, int minScore);

    // ---- Agregados por película: top por media (sin filtro de nº de votos) ----
    @Query("""
           select r.id.movieId as movieId, avg(r.score) as avg, count(r) as cnt
           from RatingEntity r
           group by r.id.movieId
           order by avg desc
           """)
    List<MovieAgg> topByAvg(Pageable pageable);

    // ---- Agregados por película: top por conteo ----
    @Query("""
           select r.id.movieId as movieId, avg(r.score) as avg, count(r) as cnt
           from RatingEntity r
           group by r.id.movieId
           order by cnt desc
           """)
    List<MovieAgg> topByCount(Pageable pageable);

    // === ALIAS con los nombres que llama AdminStatsController ===
    default List<MovieAgg> topMoviesByCount(Pageable pageable) {
        return topByCount(pageable);
    }

    // top por media con filtro de nº mínimo de votos (min): HAVING count >= :min
    @Query("""
           select r.id.movieId as movieId, avg(r.score) as avg, count(r) as cnt
           from RatingEntity r
           group by r.id.movieId
           having count(r) >= :min
           order by avg desc
           """)
    List<MovieAgg> topMoviesByAvg(@Param("min") long min, Pageable pageable);

    // ---- Agregados por usuario: top raters ----
    @Query("""
           select r.id.userId as userId, avg(r.score) as avg, count(r) as cnt
           from RatingEntity r
           group by r.id.userId
           order by cnt desc
           """)
    List<UserAgg> topRaters(Pageable pageable);

    // ---- Agregados para RatingService ----
    @Query("select avg(r.score) from RatingEntity r where r.id.movieId = :movieId")
    Double avgByMovie(@Param("movieId") UUID movieId);

    @Query("select count(r) from RatingEntity r where r.id.movieId = :movieId")
    Long countByMovie(@Param("movieId") UUID movieId);

    // ---- Borrado por usuario (AccountController) ----
    @Transactional
    @Modifying
    void deleteAllByIdUserId(UUID userId);
}
