package com.acme.reco.data;

import com.acme.reco.domain.model.Movie;

import java.util.List;
import java.util.UUID;

public class InMemoryMovieCatalog {

    public static final UUID MATRIX        = UUID.fromString("00000000-0000-0000-0000-000000000101");
    public static final UUID INCEPTION     = UUID.fromString("00000000-0000-0000-0000-000000000102");
    public static final UUID INTERSTELLAR  = UUID.fromString("00000000-0000-0000-0000-000000000103");
    public static final UUID TOY_STORY     = UUID.fromString("00000000-0000-0000-0000-000000000104");
    public static final UUID GODFATHER     = UUID.fromString("00000000-0000-0000-0000-000000000105");
    public static final UUID SPIRITED_AWAY = UUID.fromString("00000000-0000-0000-0000-000000000106");
    public static final UUID ALIEN         = UUID.fromString("00000000-0000-0000-0000-000000000107");
    public static final UUID BLADE_RUNNER  = UUID.fromString("00000000-0000-0000-0000-000000000108");

    public static List<Movie> sample() {
        return List.of(
                new Movie(MATRIX, "The Matrix", 1999,
                        List.of("Sci-Fi","Action"), List.of("hacker","simulacion","ai"),
                        "Un hacker descubre que su realidad es una simulación controlada por máquinas."),
                new Movie(INCEPTION, "Inception", 2010,
                        List.of("Sci-Fi","Thriller"), List.of("suenos","heist","mente"),
                        "Un ladrón se infiltra en los sueños para implantar ideas."),
                new Movie(INTERSTELLAR, "Interstellar", 2014,
                        List.of("Sci-Fi","Drama"), List.of("espacio","agujero_negro","familia"),
                        "Exploradores viajan a través de un agujero de gusano para salvar a la humanidad."),
                new Movie(TOY_STORY, "Toy Story", 1995,
                        List.of("Animation","Family"), List.of("juguetes","amistad"),
                        "Los juguetes cobran vida cuando los humanos no están."),
                new Movie(GODFATHER, "The Godfather", 1972,
                        List.of("Crime","Drama"), List.of("mafia","familia"),
                        "La historia de la familia criminal Corleone."),
                new Movie(SPIRITED_AWAY, "Spirited Away", 2001,
                        List.of("Animation","Fantasy"), List.of("baño_espiritus","viaje"),
                        "Chihiro entra en un mundo de espíritus para salvar a sus padres."),
                new Movie(ALIEN, "Alien", 1979,
                        List.of("Horror","Sci-Fi"), List.of("nave","xenomorfo","suspense"),
                        "La tripulación de la Nostromo se enfrenta a una forma de vida mortal."),
                new Movie(BLADE_RUNNER, "Blade Runner", 1982,
                        List.of("Sci-Fi","Noir"), List.of("androides","distopia","identidad"),
                        "Un blade runner persigue replicantes en un futuro lluvioso y decadente.")
        );
    }
}
