package com.aluracursos.literalura.enumerador;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import lombok.Getter;

import java.io.IOException;
public enum Categoria {

    FICCION("fiction", "Ficcion"),
    CIENCIA_FICCION("science fiction", "Ciencia Ficcion"),
    FANTASIA("fantasy", "Fantasia"),
    MISTERIO("mystery", "Misterio"),
    THRILLER("thriller", "Thriller"),
    ROMANCE("romance", "Romance"),
    BIOGRAFIA("biography", "Biografia"),
    AUTOBIOGRAFIA("autobiography", "Autobiografia"),
    HISTORIA("history", "Historia"),
    CIENCIA("science", "Ciencia"),
    TECNOLOGIA("technology", "Tecnologia"),
    FILOSOFIA("philosophy", "Filosofia"),
    PSICOLOGIA("psychology", "Psicologia"),
    ARTE("art", "Arte"),
    MUSICA("music", "Musica"),
    POESIA("poetry", "Poesia"),
    DRAMA("drama", "Drama"),
    NEGOCIOS("business", "Negocios"),
    ECONOMIA("economics", "Economia"),
    POLITICA("politics", "Politica"),
    RELIGION("religion", "Religion"),
    AUTOAYUDA("self-help", "Autoayuda"),
    SALUD("health", "Salud"),
    DEPORTES("sports", "Deportes"),
    VIAJES("travel", "Viajes"),
    COCINA("cooking", "Cocina"),
    INFANTIL("children's", "Infantil"),
    JUVENIL("young adult", "Juvenil"),
    EDUCATIVO("educational", "Educativo"),
    AVENTURA("adventure", "Aventura"),
    HUMOR("humor", "Humor"),
    MEDICINA("medicine", "Medicina"),
    DERECHO("law", "Derecho"),
    OTRO("other", "Otro");

    @Getter
    private final String enIngles;
    @Getter
    private final String enEspañol;

    Categoria(String enIngles, String enEspañol) {
        this.enIngles = enIngles;
        this.enEspañol = enEspañol;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.enIngles.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        return OTRO; // Retorna el valor por defecto si no se encuentra coincidencia
    }

    public static Categoria fromEspanol(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.enEspañol.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        return OTRO; // Retorna el valor por defecto si no se encuentra coincidencia
    }
}
