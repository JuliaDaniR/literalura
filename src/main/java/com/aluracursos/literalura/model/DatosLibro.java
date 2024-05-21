package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
        @JsonAlias("title") String titulo,
        @JsonAlias("id") Long id,
        @JsonAlias("authors") List<DatosAutor> listaAutores,
        @JsonAlias("languages") List<String> lenguajes,
        @JsonAlias("subjects") List<String> temas,
        @JsonAlias("formats") Map<String, String> formatos,
        @JsonAlias("download_count") Integer cantidadDescargas,
        @JsonAlias("media_type") String tipoDeMedio

) {
}
