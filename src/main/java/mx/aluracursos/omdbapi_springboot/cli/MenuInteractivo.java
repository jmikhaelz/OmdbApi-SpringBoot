package mx.aluracursos.omdbapi_springboot.cli;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import mx.aluracursos.omdbapi_springboot.client.OmdbApiClient;
import mx.aluracursos.omdbapi_springboot.client.OmdbQueryParams;
import mx.aluracursos.omdbapi_springboot.config.OmdbApiProperties;
import mx.aluracursos.omdbapi_springboot.models.Episode;
import mx.aluracursos.omdbapi_springboot.models.Season;
import mx.aluracursos.omdbapi_springboot.models.Serie;
import mx.aluracursos.omdbapi_springboot.service.OmdbApiService;

@Component
public class MenuInteractivo implements CommandLineRunner {
    private final static Scanner scanner = new Scanner(System.in);
    private static Serie serieCache;
    private static List<Season> seasonsCache = new ArrayList<>();
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

            OmdbQueryParams params = new OmdbQueryParams.Builder()
                    .titulo(scanner.nextLine()).build();
            System.out.println("\n⏳ Cargando información...\n");
            if (obtenerDatosSerie(buildOmdUri(params, omdbApiProperties), omdbApiProperties)) {
                BarraCargaUtil.mostrarBarraCarga(30, 100);
                System.out.println("✅ Datos Obtenidos de la Serie: " + params.getTitulo());
                return true;
            } else {
                BarraCargaUtil.mostrarBarraCarga(30, 100);
                System.out.println("❌ Datos Obtenidos de la Serie: " + params.getTitulo());
                System.out.print("¿Quieres seguir con otra búsqueda? [Ingrese Y/ Si no de ENTER]: ");
                var respuesta = scanner.nextLine().trim().toLowerCase();
                if (!(respuesta.equals("y")))
                    System.exit(0);;

            }
        }

    }

    private static URI buildOmdUri(OmdbQueryParams params, OmdbApiProperties omdbApiProperties) {
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

    private static boolean obtenerDatosSerie(URI uri, OmdbApiProperties omdbApiProperties) {
        try {
            var resquest = new OmdbApiClient().getJSOString(uri);
            serieCache = new OmdbApiService().getAbout(resquest, Serie.class);
            if (serieCache.titulo() != null) {
                for (int i = 1; i <= serieCache.totalTemporadas(); i++) {
                    OmdbQueryParams params = new OmdbQueryParams.Builder()
                            .titulo(serieCache.titulo()).temporada(i).build();
                    String resquest_ep;
                    try {
                        resquest_ep = new OmdbApiClient().getJSOString(buildOmdUri(params, omdbApiProperties));
                        var query_ep = new OmdbApiService().getAbout(resquest_ep, Season.class);
                        seasonsCache.add(query_ep);
                    } catch (Exception e) {
                        System.out.println("[Season " + i + "] Error de conseguir temporada : " + e.getMessage());
                    }
                }
                return true;
            }
            return false;
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
            System.out.println("2. Mostrar temporadas y episodios");
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
                    mostrarTemporadasYEpisodios();
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

    private static void mostrarTemporadasYEpisodios() {
        System.out.println("\n🎬 Lista de episodios:");
        seasonsCache.forEach(season -> season.episodios().forEach(ep -> System.out.println(
                "Temporada " + season.numero() + " - Episodio " + ep.numero() + ": " + ep.titulo())));
    }

    private static void rankingEpisodios() {
        List<Episode> episodios = obtenerEpisodios();

        System.out.println("\n📊 Top 5 episodios con mejor ranking:");
        episodios.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(Episode::evaluacion).reversed())
                .limit(5)
                .forEach(System.out::println);
    }

    private static void buscarEpisodioPorTitulo() {
        System.out.print("Ingrese el título del episodio a buscar: ");
        String texto = scanner.nextLine();

        List<Episode> episodios = obtenerEpisodios();
        Optional<Episode> queryTitle = episodios.stream()
                .filter(e -> e.titulo().toUpperCase().contains(texto.toUpperCase()))
                .findFirst();

        if (queryTitle.isPresent()) {
            System.out.println("📌 Episodio encontrado: " + queryTitle.get().toString());
        } else {
            System.out.println("❌ No se encontró ningún episodio con ese título.");
        }
    }

    private static void estadisticasSerie() {
        List<Episode> episodios = obtenerEpisodios();

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.evaluacion() != null && !e.evaluacion().equalsIgnoreCase("N/A"))
                .collect(Collectors.summarizingDouble(e -> {
                    try {
                        return Double.parseDouble(e.evaluacion());
                    } catch (NumberFormatException ex) {
                        return 0.0;
                    }
                }));

        System.out.println("\n📊 Estadísticas:");
        System.out.println("⭐ Episodio con mejor calificación: " + est.getMax());
        System.out.println("📉 Episodio con menor calificación: " + est.getMin());
        System.out.println("📈 Promedio de calificación: " + est.getAverage());
        System.out.println("🔢 Total de episodios evaluados: " + est.getCount());
    }

    private static List<Episode> obtenerEpisodios() {
        return seasonsCache.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(ep -> new Episode(
                                ep.titulo(),
                                ep.numero(),
                                String.valueOf(t.numero()),
                                ep.evaluacion(),
                                ep.lanzamiento())))
                .collect(Collectors.toList());
    }

}
