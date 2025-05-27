package mx.aluracursos.omdbapi_springboot.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configurable
@ConfigurationProperties(prefix = "omdbapi")
public class OmdbApiProperties {
    private String url;
    private String key;

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

}
