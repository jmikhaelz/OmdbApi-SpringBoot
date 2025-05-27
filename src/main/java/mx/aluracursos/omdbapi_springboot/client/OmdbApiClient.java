package mx.aluracursos.omdbapi_springboot.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import mx.aluracursos.omdbapi_springboot.config.OmdbApiProperties;

@Component
public class OmdbApiClient {
    private final OmdbApiProperties config = new OmdbApiProperties();
    private final HttpClient client = HttpClient.newHttpClient();

    public String getJSOString(OmdbQueryParams params) throws IOException {
        try {
            URI uri = buildOmdUri(params);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 || response.body().contains("\"Response\":\"False\"")) {
                return null;
            }
            return response.body();
        } catch (Exception e) {
            throw new IOException("Error al realizar la consulta a OMDBAPI", e);
        }
    }

    private URI buildOmdUri(OmdbQueryParams params) {
        StringBuilder urlApi = new StringBuilder(config.getUrl());

        if (params.getTitulo() != null)
            urlApi.append(URLEncoder.encode(params.getTitulo().toLowerCase(), StandardCharsets.UTF_8));
        if (params.getTemporada() != 0)
            urlApi.append("&Season=").append(params.getTemporada());
        if (params.getEpisodio() != 0)
            urlApi.append("&Episode=").append(params.getEpisodio());
        return URI.create(urlApi.toString());
    }
}
