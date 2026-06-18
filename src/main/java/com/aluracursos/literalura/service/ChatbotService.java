package com.aluracursos.literalura.service;

import com.aluracursos.literalura.dto.ChatbotResponseDTO;
import com.aluracursos.literalura.dto.LibroDTO;
import com.aluracursos.literalura.model.Libro;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    @Autowired
    private ResumenIAService resumenIAService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private ConversorAClaseLibroService conversorAClaseLibroService;

    public ChatbotResponseDTO procesarMensaje(String mensajeUsuario) {
        String jsonIA = resumenIAService.chatearConBibliotecario(mensajeUsuario);
        
        String mensajeBibliotecario = "Hubo un error de comunicación mágico.";
        List<String> titulosRecomendados = new ArrayList<>();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonIA);
            
            if (root.has("mensaje")) {
                mensajeBibliotecario = root.get("mensaje").asText();
            }
            if (root.has("titulos")) {
                for (JsonNode tituloNode : root.get("titulos")) {
                    titulosRecomendados.add(tituloNode.asText());
                }
            }
        } catch (Exception e) {
            System.err.println("Error parseando respuesta JSON del chatbot: " + e.getMessage());
            System.err.println("Respuesta raw: " + jsonIA);
        }

        List<Libro> librosEncontrados = new ArrayList<>();
        
        // Buscar los libros en Gutendex o en nuestra BD
        for (String titulo : titulosRecomendados) {
            try {
                System.out.println("[Chatbot] Buscando libro recomendado: " + titulo);
                
                // Primero intentamos buscar en la base de datos local
                List<Libro> busquedaLocal = libroService.listarLibrosPorNombre(titulo);
                
                if (busquedaLocal != null && !busquedaLocal.isEmpty()) {
                    librosEncontrados.add(busquedaLocal.get(0)); // Añadimos solo el mejor resultado
                } else {
                    // Si no está en local, lo buscamos en Gutendex (y se guardará automáticamente)
                    System.out.println("[Chatbot] Libro no encontrado localmente, buscando en Gutendex...");
                    String url = "https://gutendex.com/books/?search=" + titulo.replace(" ", "%20");
                    List<Libro> busquedaApi = conversorAClaseLibroService.consultaApi(url);
                    
                    if (busquedaApi != null && !busquedaApi.isEmpty()) {
                        librosEncontrados.add(busquedaApi.get(0));
                    } else {
                        System.out.println("[Chatbot] Libro no encontrado en Gutendex: " + titulo);
                    }
                }
            } catch (Exception e) {
                System.err.println("[Chatbot] Error buscando libro recomendado: " + titulo);
                e.printStackTrace();
            }
        }

        List<LibroDTO> librosDTO = librosEncontrados.stream()
                .map(LibroDTO::fromEntity)
                .collect(Collectors.toList());

        return new ChatbotResponseDTO(mensajeBibliotecario, librosDTO);
    }
}
