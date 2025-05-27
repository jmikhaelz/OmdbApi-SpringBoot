package mx.aluracursos.omdbapi_springboot.client;

public class OmdbQueryParams {
    private String titulo;
    private Integer temporada;
    private Integer episodio;

    public static class Builder {

        private String titulo;
        private Integer temporada;
        private Integer episodio;

        public Builder titulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public Builder temporada(Integer temporada) {
            this.temporada = temporada;
            return this;
        }

        public Builder episodio(Integer episodio) {
            this.episodio = episodio;
            return this;
        }

        public OmdbQueryParams build() {
            return new OmdbQueryParams(this);
        }
    }

    public OmdbQueryParams(Builder builder) {
        this.titulo = builder.titulo;
        this.temporada = builder.temporada;
        this.episodio = builder.episodio;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getTemporada() {
        return temporada;
    }

    public int getEpisodio() {
        return episodio;
    }

}
