package mx.aluracursos.omdbapi_springboot.cli;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import mx.aluracursos.omdbapi_springboot.client.OmdbApiClient;
import mx.aluracursos.omdbapi_springboot.client.OmdbQueryParams;
import mx.aluracursos.omdbapi_springboot.config.OmdbApiProperties; 
import mx.aluracursos.omdbapi_springboot.models.Serie;
import mx.aluracursos.omdbapi_springboot.service.OmdbApiService;

@Component
public class MenuInteractivo implements CommandLineRunner {
    private final static Scanner scanner = new Scanner(System.in);
    private static Serie serieCache;
    private static OmdbQueryParams params;
    
    @Autowired
    private OmdbApiProperties omdbApiProperties = new OmdbApiProperties();

    @Override
    public void run(String... args) {
        String respuesta = null;
        while (true) {
            if (solicitarNombreSerie())
                mostrarMenu();

            System.out.print("¿Quieres seguir con otra búsqueda? [Ingrese Y/ Si no de ENTER]: ");
            respuesta = scanner.nextLine().trim().toLowerCase();
            if (!(respuesta.equals("y")))
                break;
        }
        System.out.println("¡Finalizado exitosamente el programa!");
    }

    private boolean solicitarNombreSerie() {
        while (true) {
            System.out.print("\n🔎 Ingrese el nombre de la serie: ");

            params = new OmdbQueryParams.Builder()
                    .titulo(scanner.nextLine()).build();
            System.out.println("\n⏳ Cargando información...\n");
            BarraCargaUtil.mostrarBarraCarga(30, 100);
            if (obtenerDatosSerie(buildOmdUri(params, omdbApiProperties))) {
                System.out.println("✅ Datos Obtenidos de la Serie: " + params.getTitulo());
                return true;
            } else {
                System.out.println("❌ Datos Obtenidos de la Serie: " + params.getTitulo());
            }
        }

    }

    private URI buildOmdUri(OmdbQueryParams params, OmdbApiProperties omdbApiProperties) {
        StringBuilder urlApi = new StringBuilder(omdbApiProperties.getUrl());

        if (params.getTitulo() != null)
            urlApi.append("?t=").append(URLEncoder.encode(params.getTitulo().toLowerCase(), StandardCharsets.UTF_8));
        if (params.getTemporada() != 0)
            urlApi.append("&Season=").append(params.getTemporada());
        if (params.getEpisodio() != 0)
            urlApi.append("&Episode=").append(params.getEpisodio());
        
        urlApi.append("&apikey=").append(omdbApiProperties.getKey());
        return URI.create(urlApi.toString());
    }

    private static boolean obtenerDatosSerie(URI uri) {
        try {
            var resquest = new OmdbApiClient().getJSOString(uri);
            serieCache = new OmdbApiService().getAbout(resquest, Serie.class);
            return (serieCache.titulo() != null) ? true : false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n| " + serieCache.titulo() + "  \t\t|");
            System.out.println("1. Mostrar información de la serie");
            System.out.println("2. Obtener temporadas y episodios");
            System.out.println("3. Ranking de episodios");
            System.out.println("4. Buscar episodio por título");
            System.out.println("5. Estadísticas de la serie");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    mostrarInformacion();
                    break;
                case 2:
                    obtenerTemporadasYEpisodios();
                    break;
                case 3:
                    rankingEpisodios();
                    break;
                case 4:
                    buscarEpisodioPorTitulo();
                    break;
                case 5:
                    estadisticasSerie();
                    break;
                case 6:
                    System.out.println("🚀 Saliendo del programa...");
                    break;
                default:
                    System.out.println("❌ Opción inválida, intenta nuevamente.");
            }
        } while (opcion != 6);
    }

    private static void mostrarInformacion() {
        System.out.println("\n📺 Información de la serie:");
        System.out.println("Título: " + serieCache.titulo());
        System.out.println("Lanzamiento: " + serieCache.lanzamiento());
        System.out.println("Calificación: " + serieCache.evaluacion());
        System.out.println("Total de temporadas: " + serieCache.totalTemporadas());
    }

    private static void obtenerTemporadasYEpisodios() {
    }

    private static void rankingEpisodios() {
    }

    private static void buscarEpisodioPorTitulo() {
    }

    private static void estadisticasSerie() {
    }
}
