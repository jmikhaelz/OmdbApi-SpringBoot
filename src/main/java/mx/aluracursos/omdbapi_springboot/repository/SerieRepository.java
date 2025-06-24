package mx.aluracursos.omdbapi_springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.aluracursos.omdbapi_springboot.models.Categoria;
import mx.aluracursos.omdbapi_springboot.models.SerieClass;

public interface SerieRepository extends JpaRepository<SerieClass, Long> {
    Optional<SerieClass> findByTituloContainsIgnoreCase(String nombreSerie);

    List<SerieClass> findTop5ByOrderByEvaluacionDesc();

    List<SerieClass> findByGenero(Categoria categoria);

    List<SerieClass> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalTemporadas,
            Double evaluacion);
}