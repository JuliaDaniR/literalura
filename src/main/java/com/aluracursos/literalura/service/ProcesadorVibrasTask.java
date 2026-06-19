package com.aluracursos.literalura.service;

import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.aluracursos.literalura.repository.IAutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProcesadorVibrasTask {

    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;
    private final ResumenIAService resumenIAService;

    @Autowired
    public ProcesadorVibrasTask(ILibroRepository libroRepo, IAutorRepository autorRepo, ResumenIAService resumenIAService) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
        this.resumenIAService = resumenIAService;
    }

    // Se ejecuta cada 5 segundos (5000 ms) después de que termina la ejecución anterior
    @Scheduled(fixedDelayString = "${vibra.procesador.delay:5000}")
    public void procesarVibrasSiguienteLibro() {
        try {
            // Buscamos 1 solo libro que no tenga vibras asignadas
            List<Libro> librosSinVibras = libroRepo.findLibrosSinVibras(PageRequest.of(0, 1));
            
            if (!librosSinVibras.isEmpty()) {
                Libro l = librosSinVibras.get(0);
                
                String autorNombre = l.getAutores().isEmpty() ? "Desconocido" : l.getAutores().get(0).getNombre();
                String categoriaNombre = l.getCategoria() != null ? l.getCategoria().getEnEspañol() : "General";
                
                String vibrasTexto = resumenIAService.obtenerVibras(l.getTitulo(), autorNombre, categoriaNombre);
                
                if (vibrasTexto != null && !vibrasTexto.isBlank()) {
                    String[] tags = vibrasTexto.split(",");
                    List<String> nuevasVibras = new ArrayList<>();
                    for (String tag : tags) {
                        if (!tag.trim().isEmpty()) {
                            nuevasVibras.add(tag.trim());
                        }
                    }
                    l.setVibras(nuevasVibras);
                    libroRepo.save(l);
                    System.out.println("[Trabajador Vibras] Asignadas a '" + l.getTitulo() + "': " + nuevasVibras);
                }
            }
            
            // 2. Buscamos 1 autor que no tenga descripción asignada
            List<Autor> autoresSinDesc = autorRepo.findTop10ByDescripcionIsNull();
            if (!autoresSinDesc.isEmpty()) {
                Autor a = autoresSinDesc.get(0);
                String desc = resumenIAService.obtenerDescripcionAutor(a.getNombre());
                
                if (desc != null && !desc.isBlank()) {
                    if (desc.length() > 255) desc = desc.substring(0, 252) + "...";
                    a.setDescripcion(desc);
                    autorRepo.save(a);
                    System.out.println("[Trabajador IA] Descripción de autor '" + a.getNombre() + "': " + desc);
                }
            }
        } catch (Exception e) {
            System.err.println("[Trabajador Vibras] Freno de Emergencia activado (Posible Rate Limit o desconexión). El trabajador dormirá 1 minuto.");
            try {
                // Freno de emergencia: dormir el hilo del scheduler por 60 segundos
                Thread.sleep(60000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
