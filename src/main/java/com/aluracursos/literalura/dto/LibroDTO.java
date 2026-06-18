package com.aluracursos.literalura.dto;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.Libro;

import java.util.List;
import java.util.stream.Collectors;

public record LibroDTO(
        Long id,
        String titulo,
        Integer cantidadDescargas,
        String tipoDeMedio,
        Lenguaje lenguaje,
        Categoria categoria,
        List<String> formatos,
        String imagen,
        Boolean favorito,
        Boolean estado,
        String resumen,
        List<AutorDTO> autores,
        List<String> vibras
) {
    public static LibroDTO fromEntity(Libro libro) {
        if (libro == null) return null;
        
        List<AutorDTO> autoresDTO = libro.getAutores() != null 
                ? libro.getAutores().stream().map(AutorDTO::fromEntity).collect(Collectors.toList())
                : List.of();

        return new LibroDTO(
                libro.getId(),
                libro.getTitulo(),
                libro.getCantidadDescargas(),
                libro.getTipoDeMedio(),
                libro.getLenguaje(),
                libro.getCategoria(),
                libro.getFormatos(),
                libro.getImagen(),
                libro.isFavorito(),
                libro.isEstado(),
                libro.getResumen(),
                autoresDTO,
                libro.getVibras() != null ? libro.getVibras() : List.of()
        );
    }
}
