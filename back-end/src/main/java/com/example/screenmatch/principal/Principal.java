package com.example.screenmatch.principal;

import com.example.screenmatch.*;
import com.example.screenmatch.repositorio.SerieRepository;
import com.example.screenmatch.service.ConsumoAPI;
import com.example.screenmatch.service.ConvierteDatos;
import com.example.screenmatch.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=7ee75203";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ***********************************
                    1 - Buscar series
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 mejores series
                    6 - Buscar series por categoria
                    7 - Filtrar serie por temporada y avaluación
                    8 - Buscar episodios por nombre
                    9 - Top 5 episodios por serie
                    0 - Salir
                    ***********************************
                    """;
            System.out.println(menu);
            System.out.print("Ingrese la opcion: ");
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarTop5MejoresSeries();
                    break;
                case 6:
                    buscarSeriePorCategoria();
                    break;
                case 7:
                    filtrarSeriePorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    buscarTop5MejoresEpisodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.print("Escribe el nombre de la serie que deseas buscar: ");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
//        DatosSerie datosSerie = getDatosSerie();
        mostrarSeriesBuscadas();
        System.out.print("Ingrese nombre de la serie: ");
        var nombreSerie = teclado.nextLine();
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        }

    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
//        datosSeries.add(datos);
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {

//        List<Serie> series = new ArrayList<>();
//        series = datosSeries.stream()
//                .map(d -> new Serie(d))
//                .collect(Collectors.toList());
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarSeriePorTitulo() {
        System.out.print("Ingrese nombre de la serie: ");
        var nombreSerie = teclado.nextLine();
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);
        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada.");
        }
    }

    private void buscarTop5MejoresSeries() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + " ,Evaluacion: " + s.getEvaluacion()));
    }

    private void buscarSeriePorCategoria() {
        System.out.print("Ingrese genero de serie: ");
        var genero = teclado.nextLine();
        //crear lista de series
        var categoria = Categoria.fromSpanish(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoria " + genero + " son:");
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriePorTemporadaYEvaluacion() {
        System.out.print("Ingrese el total de temporadas:");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.print("Ingrese el valor de evaluacion:");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> filtroSerie = repositorio.seriesPorTemporadasYEvaluacion(totalTemporadas, evaluacion);
        System.out.println("Series filtradas:");
        filtroSerie.forEach(s ->
                System.out.println(s.getTitulo() + " - evaluacion: " + s.getEvaluacion())
        );

    }

    private void buscarEpisodiosPorTitulo() {
        System.out.print("Escribe el nombre del episodio que deseas buscar: ");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodioPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.println(String.format("Serie: %s temporada %s Episodio %s Evaluacion %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion())));
    }
//      Metodo chatGPT
//    private void buscarTop5MejoresEpisodios() {
//        System.out.print("Escribe el nombre de la serie: ");
//        var nombreSerie = teclado.nextLine();
//        Optional<Serie> serie = repositorio.findByTituloContainsIgnoreCase(nombreSerie);
//        if (serie.isPresent()) {
//            Page<Episodio> paginaEpisodios = repositorio.top5Episodios(serie.get(), PageRequest.of(0, 5));
//            paginaEpisodios.getContent().forEach(e ->
//                    System.out.printf("Episodio: %s, Evaluación: %.2f%n", e.getTitulo(), e.getEvaluacion())
//            );
//        } else {
//            System.out.println("Serie no encontrada.");
//        }
//    }

    private void buscarTop5MejoresEpisodios() {
      buscarSeriePorTitulo();
        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach(e ->
                    System.out.println(String.format("Serie: %s - Temporada %s - Episodio %s - Evaluacion %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()
                    )));
        }
    }

}

