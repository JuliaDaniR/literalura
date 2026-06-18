package com.aluracursos.literalura.dto;

import java.util.List;

public record ChatbotResponseDTO(
        String mensaje,
        List<LibroDTO> libros
) {
}
