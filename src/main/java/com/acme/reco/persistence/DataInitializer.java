package com.acme.reco.persistence;

import com.acme.reco.persistence.mapper.MovieMapper;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Profile("cb") // solo cuando usamos este perfil
public class DataInitializer implements CommandLineRunner {

    // UUIDs fijos (idénticos a los que ya usabas)
    static final UUID MATRIX        = UUID.fromString("00000000-0000-0000-0000-000000000101");
    static final UUID INCEPTION     = UUID.fromString("00000000-0000-0000-0000-000000000102");
    static final UUID INTERSTELLAR  = UUID.fromString("00000000-0000-0000-0000-000000000103");
    static final UUID TOY_STORY     = UUID.fromString("00000000-0000-0000-0000-000000000104");
    static final UUID GODFATHER     = UUID.fromString("00000000-0000-0000-0000-000000000105");
    static final UUID SPIRITED_AWAY = UUID.fromString("00000000-0000-0000-0000-000000000106");
    static final UUID ALIEN         = UUID.fromString("00000000-0000-0000-0000-000000000107");
    static final UUID BLADE_RUNNER  = UUID.fromString("00000000-0000-0000-0000-000000000108");

    private final MovieJpaRepository movies;

    public DataInitializer(MovieJpaRepository movies) { this.movies = movies; }

    @Override public void run(String... args) {
        if (movies.count() > 0) return;

        movies.saveAll(List.of(
                MovieMapper.from(MATRIX, "The Matrix", 1999, "Un hacker descubre que su realidad es una simulación.",
                        List.of("Sci-Fi","Action"), List.of("hacker","simulacion","ai")),
                MovieMapper.from(INCEPTION, "Inception", 2010, "Un ladrón se infiltra en los sueños para implantar ideas.",
                        List.of("Sci-Fi","Thriller"), List.of("suenos","heist","mente")),
                MovieMapper.from(INTERSTELLAR, "Interstellar", 2014, "Viaje espacial para salvar la humanidad.",
                        List.of("Sci-Fi","Drama"), List.of("espacio","agujero_negro","familia")),
                MovieMapper.from(TOY_STORY, "Toy Story", 1995, "Los juguetes cobran vida.",
                        List.of("Animation","Family"), List.of("juguetes","amistad")),
                MovieMapper.from(GODFATHER, "The Godfather", 1972, "La familia criminal Corleone.",
                        List.of("Crime","Drama"), List.of("mafia","familia")),
                MovieMapper.from(SPIRITED_AWAY, "Spirited Away", 2001, "Chihiro entra en un mundo de espíritus.",
                        List.of("Animation","Fantasy"), List.of("baño_espiritus","viaje")),
                MovieMapper.from(ALIEN, "Alien", 1979, "Nostromo vs xenomorfo.",
                        List.of("Horror","Sci-Fi"), List.of("nave","xenomorfo","suspense")),
                MovieMapper.from(BLADE_RUNNER, "Blade Runner", 1982, "Replicantes en futuro distópico.",
                        List.of("Sci-Fi","Noir"), List.of("androides","distopia","identidad"))
        ));
    }
}
