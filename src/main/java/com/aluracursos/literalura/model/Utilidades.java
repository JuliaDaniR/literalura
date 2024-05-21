package com.aluracursos.literalura.model;

import org.springframework.stereotype.Component;

@Component
public class Utilidades {

    public String getFormatLabel(String formato) {
        switch (formato) {
            case "text/html":
                return "Ver en línea";
            case "audio/ogg":
            case "audio/mp4":
            case "audio/mpeg":
                return "Audiolibro";
            case "application/epub+zip":
            case "application/x-mobipocket-ebook":
                return "Descargar ebooks";
            case "application/octet-stream":
                return "Descargar zip";
            case "application/pdf":
                return "Descargar PDF";
            default:
                return "Desconocido";
        }
    }
}
