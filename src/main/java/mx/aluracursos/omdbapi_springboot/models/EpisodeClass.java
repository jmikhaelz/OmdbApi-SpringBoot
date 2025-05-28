package mx.aluracursos.omdbapi_springboot.models;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EpisodeClass {
    private Integer temporada;
    private String titulo;
    private Integer numero;
    private Double evaluacion;
    private LocalDate lanzamiento;

    public EpisodeClass(Integer temporada, Episode episodio) {
        this.temporada = temporada;
        this.titulo = episodio.titulo();
        this.numero = Integer.valueOf(episodio.numero());
        try {
            this.evaluacion = Double.valueOf(episodio.evaluacion());
        } catch (NumberFormatException e) {
            this.evaluacion = 0.0;
        }
        try {
            this.lanzamiento = LocalDate.parse(episodio.lanzamiento());
        } catch (DateTimeParseException e) {
            this.lanzamiento = null;
        }
    }

    public Integer getTemporada() {
        return temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public Integer getNumero() {
        return numero;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public LocalDate getLanzamiento() {
        return lanzamiento;
    }

}