package mx.aluracursos.omdbapi_springboot.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Component;

@Component
public class OmdbApiClient {
    private final HttpClient client = HttpClient.newHttpClient();

    public String getJSOString(URI uri) throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }
            return response.body();
        } catch (Exception e) {
            throw new IOException("Error al realizar la consulta a OMDBAPI", e);
        }
    }
}
