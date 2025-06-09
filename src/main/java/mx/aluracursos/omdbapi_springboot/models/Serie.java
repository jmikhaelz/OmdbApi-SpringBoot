package mx.aluracursos.omdbapi_springboot.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Serie(
        @JsonAlias("Title") String titulo,
        @JsonAlias("Genre") String genero,
        @JsonAlias("totalSeasons") int totalTemporadas,
        @JsonAlias("Poster") String poster,
        @JsonAlias("imdbRating") String evaluacion,
        @JsonAlias("Released") String lanzamiento) {
}
