package com.example.screenmatch.dto;

import com.example.screenmatch.model.Categoria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record SerieDTO(
        Long id,
        String titulo,
        Integer totalTemporadas,
        double evaluacion,
        String poster,
        Categoria genero,
        String actores,
        String sinopsis
) {
}
