package com.aluracursos.literalura.controller;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.aluracursos.literalura.service.LibroService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PortalController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private ILibroRepository libroRepo;

    @GetMapping("/")
    public String obtenerTodosLosLibros(ModelMap model) {
        List<Libro> libros = new ArrayList<>();
        List<Libro> masDescargados = new ArrayList<>();
        List<Autor> autores = new ArrayList<>();
        List<Libro> librosEspanol = new ArrayList<>();

        // Obtener y ordenar los libros por título, limitando a 10
        libros = libroRepo.findAllByEstadoTrue().stream() // Filtra solo los libros activos
                .sorted(Comparator.comparing(Libro::getTitulo))
                .limit(10)
                .collect(Collectors.toList());

        // Obtener y ordenar los libros más descargados, limitando a 10
        masDescargados = libroRepo.findAllByEstadoTrue().stream() // Filtra solo los libros activos
                .sorted(Comparator.comparingInt(Libro::getCantidadDescargas).reversed())
                .limit(10)
                .collect(Collectors.toList());

        // Obtener y limitar la lista de autores a 22
        autores = libroService.listarAutores().stream()
                .limit(22)
                .collect(Collectors.toList());

        // Obtener y ordenar los libros en español por cantidad de descargas, limitando a 10
        librosEspanol = libroRepo.findAllByLenguajeAndEstadoTrue(Lenguaje.ESPANOL).stream() // Filtra solo los libros activos
                .sorted(Comparator.comparingInt(Libro::getCantidadDescargas).reversed())
                .limit(10)
                .collect(Collectors.toList());

        // Añadir los atributos al modelo
        model.addAttribute("librosEspanol", librosEspanol);
        model.addAttribute("autores", autores);
        model.addAttribute("libros", libros);
        model.addAttribute("masDescargados", masDescargados);
        model.addAttribute("tipos", Categoria.values());
        model.addAttribute("lenguajes", Lenguaje.values());

        return "index.html";
    }

}
