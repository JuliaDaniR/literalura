package com.aluracursos.literalura.controller;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.service.LibroService;
import com.aluracursos.literalura.service.LibroServiceAsync;
import java.util.ArrayList;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;
    
    private final LibroServiceAsync serviceAsync;

    @Autowired
    public LibroController(LibroServiceAsync serviceAsync) {
        this.serviceAsync = serviceAsync;
    }

    @GetMapping("/detalles/{id}")
    public String obtenerPorId(@PathVariable Long id, ModelMap model) {

        Libro libro = libroService.obtenerPorId(id);
        System.out.println("libro "+libro);
        model.addAttribute("libro", libro);
        
        return "detalles.html";
    }

    @GetMapping("/listar")
    public String realizarFiltros(ModelMap model,
            RedirectAttributes redirectAttrs,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "palabraClave", required = false) String palabraClave,
            @RequestParam(value = "inicial", required = false) String inicial,
            @RequestParam(value = "tipoCategoria", required = false) Categoria tipoCategoria,
            @RequestParam(value = "idioma", required = false) Lenguaje idioma,
            @RequestParam(value = "masPopulares", required = false) String masPopulares,
            @RequestParam(value = "librosGuardados", required = false) String librosGuardados,
            @RequestParam(value = "listarAutores", required = false) String listarAutores,
            @RequestParam(value = "nombreAutor", required = false) String nombreAutor,
            @RequestParam(value = "anio", required = false) Integer anio) {

        System.out.println("Nombre= " + nombre);
        System.out.println("Palabra clave= " + palabraClave);
        System.out.println("inicial= " + inicial);
        System.out.println("tipoCategoria= " + tipoCategoria);
        System.out.println("idioma= " + idioma);
        System.out.println("masPopulares= " + masPopulares);
        System.out.println("librosGuardados= " + librosGuardados);
        System.out.println("lista autores= " + listarAutores);
        System.out.println("nombre autor= " + nombreAutor);
        System.out.println("año= " + anio);
        List<Libro> listadoLibros = new ArrayList<>();
        List<Autor> listadoAutores = new ArrayList<>();
        String textoResultado = "";
        
        try {
            if (nombre != null && !nombre.isEmpty()) {
                listadoLibros = libroService.listarLibrosPorNombre(nombre);  
                textoResultado = "Estos son los libros encontrados en base al nombre que ingresó";
            }
            if (palabraClave != null && !palabraClave.isEmpty()) {
                listadoLibros = libroService.listarLibroPorPalabraClave(palabraClave);
                textoResultado = "Estos son los libros encontrados relacionados a la palabra que ingresó";
            }
            if (inicial != null && !inicial.isEmpty()) {
                listadoLibros = libroService.listarLibrosPorInicial(inicial);
                textoResultado = "Estos son los libros que comienzan con la letra '" + inicial.toUpperCase() + "'";
            }
            if (tipoCategoria != null) {
                String categoria = tipoCategoria.name();
                textoResultado = "Estos son los libros encontrados para la categoria '" + categoria.toUpperCase() + "'";
                listadoLibros = libroService.listarLibroPorTema(categoria);
            }
            if (idioma != null) {
                String lenguaje = idioma.getLanguageName();
                listadoLibros = libroService.listarLibroPorIdioma(lenguaje);
                textoResultado = "Estos son los libros encontrados para el idioma '" + lenguaje.toUpperCase() + "'";
            }
            if (listarAutores != null && !listarAutores.isEmpty()) {
                listadoAutores = libroService.listarAutores();
                textoResultado = "Estos son los autores encontrados seleccione para ver su biografia";
            }
            if (masPopulares != null && !masPopulares.isEmpty()) {
                listadoLibros = libroService.listar100LibrosMasDescargados();
                textoResultado = "Estos son los 100 libros más populares";
            }
            if (librosGuardados != null && !librosGuardados.isEmpty()) {
                listadoLibros = libroService.listarLibros();
                textoResultado = "Estos son los libros que tiene guardados";
            }
            if (nombreAutor != null && !nombreAutor.isEmpty()) {
                listadoLibros = libroService.listarLibroPorAutor(nombreAutor);
                textoResultado = "Estos son los libros encontrados en base al nombre del autor que ingreso";
            }
            if (anio != null) {
                listadoLibros = libroService.listarLibrosDeAutoresVivosPorAnio(anio);
                textoResultado = "Estos son los libros encontrados de autores vivos en base al año que ingreso";
            }
            redirectAttrs.addFlashAttribute("exito", "Estos son sus resultados de su búsqueda");
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("error", "No se encontró ningún resultado");
        }

        model.addAttribute("listadoLibros", listadoLibros);
        model.addAttribute("listadoAutores", listadoAutores);
        model.addAttribute("textoResultado", textoResultado);
        
        return "resultado.html";
    }

}
