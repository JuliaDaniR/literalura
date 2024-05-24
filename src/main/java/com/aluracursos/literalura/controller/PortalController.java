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
        List<Autor> autores =  new ArrayList<>();
        List<Libro> librosEspanol =new ArrayList<>();
        
        if(libroRepo.findAll().isEmpty()){
        libros = libroService.listarLibros().stream().sorted(Comparator.comparing(Libro::getTitulo)).limit(10).collect(Collectors.toList());
        masDescargados = libroService.listar100LibrosMasDescargados().stream().limit(10).collect(Collectors.toList());
        autores = libroService.listarAutores().stream().limit(22).collect(Collectors.toList());
        librosEspanol = libroService.librosEnEspañol().stream().limit(10).collect(Collectors.toList());
        }else{
        libros = libroRepo.findAll().stream().sorted(Comparator.comparing(Libro::getTitulo)).limit(10).collect(Collectors.toList());
        masDescargados = libroRepo.findAll().stream().sorted(Comparator.comparingInt(Libro::getCantidadDescargas).reversed()).limit(10).collect(Collectors.toList());
        autores = libroService.listarAutores().stream().limit(22).collect(Collectors.toList());
        librosEspanol = libroRepo.findAll().stream()
               .filter(libro -> libro.getLenguaje() == Lenguaje.ESPANOL)
               .sorted(Comparator.comparingInt(Libro::getCantidadDescargas).reversed())
                .limit(10)
                .collect(Collectors.toList());
        }
        model.addAttribute("librosEspanol", librosEspanol);
        model.addAttribute("autores", autores);
        model.addAttribute("libros", libros);
        model.addAttribute("masDescargados", masDescargados);
        model.addAttribute("tipos", Categoria.values());
        model.addAttribute("lenguajes", Lenguaje.values());

        return "index.html";
    }
}
