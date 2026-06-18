package com.aluracursos.literalura.dto;

import com.aluracursos.literalura.model.Autor;

public record AutorDTO(
        Long id,
        String nombre,
        Integer anioNac,
        Integer anioMuerte,
        String nombreFormateado
) {
    public static AutorDTO fromEntity(Autor autor) {
        if (autor == null) return null;
        return new AutorDTO(
                autor.getId(),
                autor.getNombre(),
                autor.getAnioNac(),
                autor.getAnioMuerte(),
                autor.getNombreFormateado()
        );
    }
}
