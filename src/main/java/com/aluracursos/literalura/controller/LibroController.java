package com.aluracursos.literalura.controller;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.model.LibrosEliminados;
import com.aluracursos.literalura.repository.ILibroEliminadoRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.aluracursos.literalura.service.LibroService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private ILibroEliminadoRepository libroEliminadoRepo;

    @Autowired
    private ILibroRepository libroRepo;

    @GetMapping("/detalles/{id}")
    public String obtenerPorId(@PathVariable Long id, ModelMap model) {

        Libro libro = libroService.obtenerPorId(id);
        Map<String, String> formatos = libroService.obtenerFormatos(id);

        model.addAttribute("formatos", formatos);
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
            @RequestParam(value = "librosEliminados", required = false) String librosEliminados,
            @RequestParam(value = "librosFavoritos", required = false) String librosFavoritos,
            @RequestParam(value = "listarAutores", required = false) String listarAutores,
            @RequestParam(value = "nombreAutor", required = false) String nombreAutor,
            @RequestParam(value = "anio", required = false) Integer anio,
            @RequestHeader(value = "Referer", required = false) String referer) {

        System.out.println("********************* " + librosEliminados);
        List<Libro> listadoLibros = new ArrayList<>();
        List<Autor> listadoAutores = new ArrayList<>();
        List<Libro> listaEliminados = new ArrayList<>();
        boolean buscandoLibros = true;
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
                buscandoLibros = false;
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
            if (librosEliminados != null && !librosEliminados.isEmpty()) {
                List<LibrosEliminados> listaEliminadosEntidades = libroService.listarEliminados();
                List<Long> listaEliminadosIds = listaEliminadosEntidades.stream()
                        .map(LibrosEliminados::getLibroId) // Asegúrate de que este método obtenga el ID del libro original
                        .collect(Collectors.toList());

                for (Long id : listaEliminadosIds) {
                    Optional<Libro> libro = libroRepo.findById(id);
                    System.out.println("libro optional " + libro.get().getTitulo());
                    if (libro.isPresent()) {
                        listaEliminados.add(libro.get());
                    }
                }
                buscandoLibros = false;
                textoResultado = "Estos son los libros que usted ha eliminado";
            }
            if (librosFavoritos != null && !librosFavoritos.isEmpty()){
                listadoLibros = libroService.listarFavoritos();
                textoResultado = "Estos son tus favoritos";
            }
            redirectAttrs.addFlashAttribute("exito", "Estos son sus resultados de su búsqueda");
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("error", "No se encontró ningún resultado");
        }

        model.addAttribute("listadoLibros", listadoLibros);
        model.addAttribute("listadoAutores", listadoAutores);
        model.addAttribute("textoResultado", textoResultado);
        model.addAttribute("listaEliminados", listaEliminados);
        model.addAttribute("buscandoLibros", buscandoLibros);

        return "resultado.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id, @RequestHeader(value = "Referer", required = false) String referer) {
        Libro libro = libroService.obtenerPorId(id);
        if (libro != null) {

            LibrosEliminados libroEliminado = new LibrosEliminados();
            libroEliminado.setLibroId(libro.getId());

            // Guardar el registro del libro eliminado en la base de datos
            libroEliminadoRepo.save(libroEliminado);

            // Cambiar el estado del libro a false
            libroService.eliminarLibro(id);

        }
        return "redirect:" + (referer != null ? referer : "/");
    }

    @GetMapping("/restaurar/{id}")
    public String restaurarLibro(@PathVariable Long id, @RequestHeader(value = "Referer", required = false) String referer) {
        libroService.restaurarLibro(id);
        return "redirect:" + (referer != null ? referer : "/");
    }
        
    @PostMapping("/favorito/{id}")
    public ResponseEntity<Void> toggleFavorito(@PathVariable Long id, @RequestHeader(value = "Referer", required = false) String referer) {
        try {
            Libro libro = libroService.obtenerPorId(id);
            if (libro != null) {
                libro.setFavorito(!libro.isFavorito());
                libroRepo.save(libro);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Registro del error para depuración
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

   
}
