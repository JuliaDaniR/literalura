package com.aluracursos.literalura.controller;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.aluracursos.literalura.service.LibroService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    //Agregue esto para poder agilizar la carga de la pagina principal
    Pageable top10Pageable = PageRequest.of(0, 10);
    Pageable top22Pageable = PageRequest.of(0, 22);

    List<Libro> libros = libroRepo.findTop10ByEstadoTrueOrderByTitulo(top10Pageable);
    List<Libro> masDescargados = libroRepo.findTop10ByEstadoTrueOrderByCantidadDescargasDesc(top10Pageable);
    List<Autor> autores = libroService.listarAutores(top22Pageable);
    List<Libro> librosEspanol = libroRepo.findTop10ByLenguajeAndEstadoTrueOrderByCantidadDescargasDesc(Lenguaje.ESPANOL, top10Pageable);

    if (masDescargados.isEmpty()) {
        libroService.listar10LibrosMasDescargados();
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
