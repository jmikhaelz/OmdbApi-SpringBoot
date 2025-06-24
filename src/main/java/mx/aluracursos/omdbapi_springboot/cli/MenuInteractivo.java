package mx.aluracursos.omdbapi_springboot.cli;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import mx.aluracursos.omdbapi_springboot.client.OmdbApiClient;
import mx.aluracursos.omdbapi_springboot.client.OmdbQueryParams;
import mx.aluracursos.omdbapi_springboot.config.GeminiApiProperties;
import mx.aluracursos.omdbapi_springboot.config.OmdbApiProperties;
import mx.aluracursos.omdbapi_springboot.models.EpisodeClass;
import mx.aluracursos.omdbapi_springboot.models.Season;
import mx.aluracursos.omdbapi_springboot.models.Serie;
import mx.aluracursos.omdbapi_springboot.models.SerieClass;
import mx.aluracursos.omdbapi_springboot.repository.SerieRepository;
import mx.aluracursos.omdbapi_springboot.service.OmdbApiService;

@Component
public class MenuInteractivo implements CommandLineRunner {
    // VARIABLE LOCALES
    private final static Scanner scanner = new Scanner(System.in);
    private List<SerieClass> listHistorial;

    // VARIABLES DE PROPIEDADES
    @Autowired
    private OmdbApiProperties omdbApiProperties = new OmdbApiProperties();
    @Autowired
    private GeminiApiProperties geminiApiProperties = new GeminiApiProperties();
    @Autowired
    private SerieRepository repository;

    @Override
    public void run(String... args) {
        mostrarMenu();
        System.out.println("¡Finalizado exitosamente el programa!");
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n|\t\t SERIE PLANET \t\t|");
            System.out.println("1. Buscar serie ");
            System.out.println("2. Consultar temporadas y episodios");
            System.out.println("3. Consultar Ranking de episodios");
            System.out.println("4. Buscar episodio por título");
            System.out.println("5. Consultar estadisticas de la serie");
            System.out.println("6. Historial de Busquedas");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    solicitarNombreSerie();
                    break;
                case 2:
                    mostrarTemporadasYEpisodios();
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    historialBusqueda();
                    break;
                case 7:
                    System.out.print("¿Seguro que quieres salir? [Ingrese Y/ Si no de ENTER]: ");
                    String respuesta = scanner.nextLine().trim().toLowerCase();
                    if (!(respuesta.equals("y")))
                        opcion = 0;
                    break;
                default:
            }
        } while (opcion != 7);
    }

    private void mostrarTemporadasYEpisodios() {
        System.out.println("\n\t| SERIES DEISPONIBLES \t\t|");
        serieDisponible();
        System.out.println("Ingrese el nombre de la serie que quieres ver sus capitulos: ");
        var nombreSerie = scanner.nextLine();

        Optional<SerieClass> serie = listHistorial.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<Season> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                OmdbQueryParams params = new OmdbQueryParams.Builder()
                        .titulo(serieEncontrada.getTitulo()).temporada(i).build();
                String resquestEp;
                try {
                    resquestEp = new OmdbApiClient().getJSOString(params, omdbApiProperties);
                    var queryEp = new OmdbApiService().getAbout(resquestEp, Season.class);
                    temporadas.add(queryEp);
                } catch (Exception e) {
                    System.out.println("[Season " + i + "] Error de conseguir temporada : " + e.getMessage());
                }
            }
            System.out.println("\n🎬 Lista de episodios:");
            temporadas.forEach(season -> season.episodios().forEach(ep -> System.out.println(
                    "Temporada " + season.numero() + " - Episodio " + ep.numero() + ": " + ep.titulo())));
            List<EpisodeClass> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new EpisodeClass(t.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        } else

        {
            System.out.println("[X] Error no existe la serie " + nombreSerie);
        }
    }

    private void solicitarNombreSerie() {
        while (true) {
            System.out.print("\n🔎 Ingrese el nombre de la serie: ");
            OmdbQueryParams params = new OmdbQueryParams.Builder()
                    .titulo(scanner.nextLine().toLowerCase()).build();
            System.out.println("\n⏳ Cargando información...\n");
            if (obtenerDatosSerie(params)) {
                System.out.println("✅ Datos Obtenidos de la Serie: " + params.getTitulo().toUpperCase());
                break;
            } else {
                System.out.println("❌ Datos Obtenidos de la Serie: " + params.getTitulo());
                System.out.print("¿Quieres seguir con otra búsqueda? [Ingrese Y/ Si no de ENTER]: ");
                var respuesta = scanner.nextLine().trim().toLowerCase();
                if (!(respuesta.equals("y")))
                    break;

            }
        }

    }

    private boolean obtenerDatosSerie(OmdbQueryParams uri) {
        try {
            var resquest = new OmdbApiClient().getJSOString(uri, omdbApiProperties);
            Serie serieCache = new OmdbApiService().getAbout(resquest, Serie.class);
            if (serieCache.titulo() != null) {
                SerieClass consultaSerie = new SerieClass(serieCache, geminiApiProperties);
                repository.save(consultaSerie);
                BarraCargaUtil.mostrarBarraCarga(30, 100);
                System.out.println(consultaSerie);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void serieDisponible() {
        listHistorial = repository.findAll();

        listHistorial.stream()
                .sorted(Comparator.comparing(SerieClass::getTitulo))
                .forEach(s -> System.out.println("> " + s.getTitulo() + " - " + s.getLanzamiento()));
    }

    private void historialBusqueda() {
        System.out.println("\n\t| HISTORIAL DE BUSQUEDAS \t\t|");
        listHistorial = repository.findAll();

        listHistorial.stream()
                .sorted(Comparator.comparing(SerieClass::getGenero))
                .forEach(System.out::println);
    }
}