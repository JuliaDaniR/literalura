package com.aluracursos.literalura.enumerador;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.io.IOException;

@JsonDeserialize(using = Lenguaje.LenguajeDeserializer.class)
public enum Lenguaje {

    INGLES("en", "Ingles"),
    ALEMAN("de", "Aleman"),
    FRANCES("fr", "Frances"),
    ESPANOL("es", "Español"),
    ITALIANO("it", "Italiano"),
    NEERLANDES("nl", "Neerlandes"),
    PORTUGUES("pt", "Portugues"),
    SUECO("sv", "Sueco"),
    DANES("da", "Danes"),
    NORUEGO("no", "Noruego"),
    FINLANDES("fi", "Finlandes"),
    LATIN("la", "Latín"),
    RUSO("ru", "Ruso"),
    POLACO("pl", "Polaco"),
    CHINO("zh", "Chino"),
    JAPONES("ja", "Japones"),
    OTRO("other", "Otro"); // Valor por defecto

    @Getter
    private final String code;
    @Getter
    private final String languageName;

    Lenguaje(String code, String languageName) {
        this.code = code;
        this.languageName = languageName;
    }

    public static Lenguaje fromString(String text) {
        for (Lenguaje lenguaje : Lenguaje.values()) {
            if (lenguaje.code.equalsIgnoreCase(text)) {
                return lenguaje;
            }
        }
        return OTRO; // Retorna el valor por defecto si no se encuentra coincidencia
    }

    public static Lenguaje fromEspanol(String text) {
        for (Lenguaje lenguaje : Lenguaje.values()) {
            if (lenguaje.languageName.toUpperCase().equalsIgnoreCase(text.toUpperCase())) {
                return lenguaje;
            }
        }
        return OTRO; // Retorna el valor por defecto si no se encuentra coincidencia
    }

    public static class LenguajeDeserializer extends JsonDeserializer<Lenguaje> {
        @Override
        public Lenguaje deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getText();
            return fromString(text);
        }
    }
}
