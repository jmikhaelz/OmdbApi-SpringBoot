package mx.aluracursos.omdbapi_springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.aluracursos.omdbapi_springboot.models.SerieClass;

public interface SerieRepository extends JpaRepository<SerieClass, Long> {
}