package com.example.screenmatch.repositorio;

import com.example.screenmatch.dto.EpisodioDTO;
import com.example.screenmatch.dto.SerieDTO;
import com.example.screenmatch.model.Categoria;
import com.example.screenmatch.model.Episodio;
import com.example.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

    //buscar top 5
    List<Serie> findTop5ByOrderByEvaluacionDesc();

    //buscar por categoria
    List<Serie> findByGenero(Categoria categoria);

    //buscar por temporada
//    List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalTemporadas, Double evaluacion);
    //buscar por temporada, usando jpql
    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion")
    List<Serie> seriesPorTemporadasYEvaluacion(int totalTemporadas, Double evaluacion);

    //  Buscar episodioPorNombre
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")
    List<Episodio> episodioPorNombre(String nombreEpisodio);

    //top5Episodios --methods chatgpt
//    @Query("SELECT e FROM Episodio e WHERE e.serie = :serie ORDER BY e.evaluacion DESC")
//    Page<Episodio> top5Episodios(@Param("serie") Serie serie, Pageable pageable);

    //top5Episodios
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);
    //lanzamientosMasRecientes
    @Query("SELECT s FROM Serie s "+"JOIN s.episodios e "+ "GROUP BY s "+"ORDER BY MAX(e.fechaDeLanzamiento) DESC LIMIT 5")
    List<Serie> lanzamientosMasRecientes();

    //obtenerTemporadaPorNumero
    @Query("SELECT e FROM Serie s Join s.episodios e WHERE s.id =:id AND e.temporada = :numeroTemporada")
    List<Episodio> obtenerTemporadaPorNumero(Long id, Long numeroTemporada);

}
