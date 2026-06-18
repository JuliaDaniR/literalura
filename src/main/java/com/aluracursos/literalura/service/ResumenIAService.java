package com.aluracursos.literalura.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ResumenIAService {

    @Value("${openrouter.api-key}")
    private String openRouterApiKey;

    @Value("${openrouter.url}")
    private String openRouterUrl;

    @Value("${openrouter.models}")
    private String openRouterModelsRaw;

    @Value("${gemini.base-url}")
    private String geminiBaseUrl;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    @Value("${gemini.models}")
    private String geminiModelsRaw;

    public String obtenerVibras(String titulo, String autor, String categoria) {
        String prompt = "Actúa como un analista literario para una biblioteca moderna. Para el libro '" + titulo + "' de '" + autor + "' (categoría: " + categoria + "), genera exactamente 3 etiquetas de subgéneros o 'vibras' que resuman los sentimientos o la temática del libro (por ejemplo: #MisterioGótico, #AventuraÉpica, #RomanceTrágico). Responde ÚNICAMENTE con las 3 etiquetas separadas por comas, sin usar símbolos de numeral (#) y sin ningún otro texto. Por ejemplo: Misterio Gótico, Aventura Épica, Romance Trágico.";

        String[] openRouterModels = openRouterModelsRaw.split(",");
        String[] geminiModels = geminiModelsRaw.split(",");
        
        System.out.println("--- Generando Vibras con IA para: " + titulo + " ---");

        for (String modelo : openRouterModels) {
            String respuesta = intentarConOpenRouter(prompt, modelo.trim());
            if (respuesta != null && !respuesta.isEmpty()) {
                return procesarRespuestaVibras(respuesta);
            }
        }

        for (String modelo : geminiModels) {
            String respuesta = intentarConGemini(prompt, modelo.trim());
            if (respuesta != null && !respuesta.isEmpty()) {
                return procesarRespuestaVibras(respuesta);
            }
        }

        throw new RuntimeException("Todos los modelos de IA fallaron. Posible Rate Limit o problema de red.");
    }

    private String procesarRespuestaVibras(String respuesta) {
        // Limpiamos la respuesta en caso de que la IA responda con formato Markdown o puntos.
        return respuesta.replace("#", "").replace(".", "").replace("\"", "").trim();
    }

    public String chatearConBibliotecario(String mensajeUsuario) {
        String prompt = "Eres el Bibliotecario Mágico de Literalura, un experto amable en literatura clásica. " +
                "El usuario dice: '" + mensajeUsuario + "'. " +
                "Recomienda de 1 a 3 libros de dominio público que existan en Project Gutenberg. " +
                "IMPORTANTE: Tu respuesta DEBE ser estrictamente un JSON válido con esta estructura exacta y sin formato markdown: " +
                "{\"mensaje\": \"Tu respuesta amistosa en español recomendando los libros\", \"titulos\": [\"Título original en inglés o idioma original\", \"Título 2 original\"]}. " +
                "Asegúrate de que los títulos en el array 'titulos' sean precisos para poder buscarlos en una base de datos (preferiblemente su título más conocido en inglés). Nunca dejes el array 'titulos' vacío si recomiendas algo en el mensaje.";

        String[] openRouterModels = openRouterModelsRaw.split(",");
        String[] geminiModels = geminiModelsRaw.split(",");

        for (String modelo : openRouterModels) {
            String respuesta = intentarConOpenRouter(prompt, modelo.trim());
            if (respuesta != null && !respuesta.isEmpty() && respuesta.contains("{")) {
                return limpiarJson(respuesta);
            }
        }

        for (String modelo : geminiModels) {
            String respuesta = intentarConGemini(prompt, modelo.trim());
            if (respuesta != null && !respuesta.isEmpty() && respuesta.contains("{")) {
                return limpiarJson(respuesta);
            }
        }

        return "{\"mensaje\": \"Mis disculpas, la magia de la biblioteca está fallando en este momento.\", \"titulos\": []}";
    }

    private String limpiarJson(String respuesta) {
        String json = respuesta;
        if (json.contains("```json")) {
            json = json.substring(json.indexOf("```json") + 7);
            if (json.contains("```")) {
                json = json.substring(0, json.lastIndexOf("```"));
            }
        } else if (json.contains("```")) {
            json = json.substring(json.indexOf("```") + 3);
            if (json.contains("```")) {
                json = json.substring(0, json.lastIndexOf("```"));
            }
        }
        return json.trim();
    }

    public String obtenerResumen(String titulo, String autor) {
        String prompt = "Actúa como un crítico literario experto. Escribe un resumen de máximo 2 párrafos sobre el libro '" + titulo + "' de " + autor + ". El tono debe ser interesante e invitar a leerlo.";
        
        String[] openRouterModels = openRouterModelsRaw.split(",");
        String[] geminiModels = geminiModelsRaw.split(",");
        
        System.out.println("--- Iniciando Cascada de IA para: " + titulo + " ---");

        // 1. Fase OpenRouter (Prioridad por costo/límites)
        for (String modelo : openRouterModels) {
            String modeloTrimmed = modelo.trim();
            System.out.println("[OpenRouter] Intentando con modelo: " + modeloTrimmed);
            String respuesta = intentarConOpenRouter(prompt, modeloTrimmed);
            if (respuesta != null) {
                System.out.println("-> ÉXITO con OpenRouter (" + modeloTrimmed + ")");
                return respuesta;
            }
        }

        System.out.println("--- OpenRouter agotado, pasando a Google Gemini ---");

        // 2. Fase Gemini (Fallback Seguro)
        for (String modelo : geminiModels) {
            String modeloTrimmed = modelo.trim();
            System.out.println("[Gemini] Intentando con modelo: " + modeloTrimmed);
            String respuesta = intentarConGemini(prompt, modeloTrimmed);
            if (respuesta != null) {
                System.out.println("-> ÉXITO con Gemini (" + modeloTrimmed + ")");
                return respuesta;
            }
        }

        System.err.println("--- CASCADA FALLIDA: Ningún modelo pudo responder ---");
        return "Lo sentimos, nuestro bibliotecario virtual está descansando y todos los sistemas de respaldo están ocupados. Por favor, intenta de nuevo más tarde.";
    }

    private String intentarConOpenRouter(String prompt, String modelo) {
        // Evita fallar en código si la clave no está configurada como variable de entorno
        if (openRouterApiKey == null || openRouterApiKey.contains("${")) {
            System.out.println("  -> Error: API Key de OpenRouter no está configurada en las variables de entorno.");
            return null;
        }

        // OpenRouter usa el estándar de OpenAI
        String requestBody = "";
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            requestBody = mapper.writeValueAsString(java.util.Map.of(
                "model", modelo,
                "temperature", 0.7,
                "messages", java.util.List.of(
                    java.util.Map.of("role", "user", "content", prompt)
                )
            ));
        } catch (Exception e) {
            System.err.println("Error construyendo JSON OpenRouter: " + e.getMessage());
            return null;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(openRouterUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openRouterApiKey)
                    .header("HTTP-Referer", "http://localhost:8080") // OpenRouter requiere un Referer válido
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body());

            if (rootNode.has("error")) {
                System.out.println("  -> Falla OpenRouter (" + modelo + "): " + rootNode.path("error").path("message").asText());
                return null;
            }

            return rootNode.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            System.out.println("  -> Error de red/timeout en OpenRouter: " + e.getMessage());
            return null;
        }
    }

    private String intentarConGemini(String prompt, String modelo) {
        // Formato específico de Google Gemini
        String requestBody = "";
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            requestBody = mapper.writeValueAsString(java.util.Map.of(
                "contents", java.util.List.of(
                    java.util.Map.of("parts", java.util.List.of(
                        java.util.Map.of("text", prompt)
                    ))
                )
            ));
        } catch (Exception e) {
            System.err.println("Error construyendo JSON Gemini: " + e.getMessage());
            return null;
        }
        String urlConModelo = geminiBaseUrl + "/v1beta/models/" + modelo + ":generateContent?key=" + geminiApiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlConModelo))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body());

            // Capturar errores como 404 Model Not Found, o 503 Overloaded
            if (rootNode.has("error")) {
                System.out.println("  -> Falla Gemini (" + modelo + "): " + rootNode.path("error").path("message").asText());
                return null;
            }

            return rootNode.path("candidates").get(0)
                           .path("content")
                           .path("parts").get(0)
                           .path("text").asText();

        } catch (Exception e) {
            System.out.println("  -> Error de red/timeout en Gemini: " + e.getMessage());
            return null;
        }
    }
}
