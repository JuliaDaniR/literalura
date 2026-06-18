package com.aluracursos.literalura.controller;

import com.aluracursos.literalura.dto.ChatbotResponseDTO;
import com.aluracursos.literalura.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<ChatbotResponseDTO> enviarMensaje(@RequestBody Map<String, String> payload) {
        String mensaje = payload.get("mensaje");
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        ChatbotResponseDTO respuesta = chatbotService.procesarMensaje(mensaje);
        return ResponseEntity.ok(respuesta);
    }
}
