package com.aluracursos.literalura.dto;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;

public record BusquedaRequestDTO(
        String nombre,
        String palabraClave,
        String inicial,
        Categoria tipoCategoria,
        Lenguaje idioma,
        String masPopulares,
        String librosGuardados,
        String librosEliminados,
        String librosFavoritos,
        String listarAutores,
        String nombreAutor,
        Integer anio,
        String vibra
) {
}
