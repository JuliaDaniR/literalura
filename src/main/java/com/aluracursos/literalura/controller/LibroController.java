package com.aluracursos.literalura.controller;

import com.aluracursos.literalura.dto.AutorDTO;
import com.aluracursos.literalura.dto.BusquedaRequestDTO;
import com.aluracursos.literalura.dto.LibroDTO;
import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.aluracursos.literalura.service.LibroService;
import java.util.ArrayList;

import com.aluracursos.literalura.service.ResumenIAService;
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
    private ILibroRepository libroRepo;

    @Autowired
    private ResumenIAService resumenIAService;

    @GetMapping("/detalles/{id}")
    public String obtenerPorId(@PathVariable Long id, ModelMap model) {

        Libro libro = libroService.obtenerPorId(id);
        Map<String, String> formatos = libroService.obtenerFormatos(id);

        model.addAttribute("formatos", formatos);
        model.addAttribute("libro", LibroDTO.fromEntity(libro));

        return "detalles.html";
    }

    @GetMapping("/listar")
    public String realizarFiltros(ModelMap model,
            RedirectAttributes redirectAttrs,
            BusquedaRequestDTO filtros,
            @RequestParam(defaultValue = "0") int page,
            @RequestHeader(value = "Referer", required = false) String referer) {

        List<Libro> listadoLibros = new ArrayList<>();
        List<Autor> listadoAutores = new ArrayList<>();
        List<Libro> listaEliminados = new ArrayList<>();
        boolean buscandoLibros = true;
        boolean isDefaultSearch = false;
        String textoResultado = "Resultados de Búsqueda";
        String fraseMotivacional = "Explora y descubre nuevas lecturas";

        try {
            if (filtros.nombre() != null && !filtros.nombre().isEmpty()) {
                listadoLibros = libroService.listarLibrosPorNombre(filtros.nombre());
                textoResultado = "Libros por nombre: " + filtros.nombre();
                fraseMotivacional = "Cada título encierra un universo entero por descubrir.";
            } else if (filtros.nombreAutor() != null && !filtros.nombreAutor().isEmpty()) {
                listadoLibros = libroService.listarLibroPorAutor(filtros.nombreAutor());
                textoResultado = "Libros por autor: " + filtros.nombreAutor();
                fraseMotivacional = "Descubre la genialidad detrás de sus palabras.";
            } else if (filtros.palabraClave() != null && !filtros.palabraClave().isEmpty()) {
                listadoLibros = libroService.listarLibroPorPalabraClave(filtros.palabraClave());
                textoResultado = "Libros por palabra clave: " + filtros.palabraClave();
                fraseMotivacional = "Conectando conceptos y revelando historias ocultas.";
            } else if (filtros.tipoCategoria() != null) {
                String categoria = filtros.tipoCategoria().name();
                listadoLibros = libroService.listarLibroPorTema(categoria);
                textoResultado = "Categoría: " + filtros.tipoCategoria().getEnEspañol();
                
                switch (filtros.tipoCategoria()) {
                    case FANTASIA:
                        fraseMotivacional = "Mundos mágicos, seres asombrosos y aventuras sin límites te esperan.";
                        break;
                    case FICCION:
                        fraseMotivacional = "Viajes inolvidables a mundos donde la imaginación es la única regla.";
                        break;
                    case MISTERIO:
                    case THRILLER:
                        fraseMotivacional = "Suspenso, enigmas y secretos que te mantendrán al borde del asiento.";
                        break;
                    case ROMANCE:
                        fraseMotivacional = "Historias que hacen latir el corazón y suspirar al alma.";
                        break;
                    case BIOGRAFIA:
                    case AUTOBIOGRAFIA:
                        fraseMotivacional = "Vidas extraordinarias que inspiran y dejan una huella imborrable.";
                        break;
                    case HISTORIA:
                        fraseMotivacional = "Viaja en el tiempo y descubre los ecos vibrantes de nuestro pasado.";
                        break;
                    case CIENCIA:
                    case TECNOLOGIA:
                        fraseMotivacional = "Explorando los fascinantes límites del conocimiento y el universo.";
                        break;
                    default:
                        fraseMotivacional = "Sumérgete en las maravillas de este fascinante género literario.";
                }
            } else if (filtros.idioma() != null) {
                String lenguaje = filtros.idioma().getLanguageName();
                listadoLibros = libroService.listarLibroPorIdioma(lenguaje);
                textoResultado = "Idioma: " + lenguaje;
                
                switch (filtros.idioma()) {
                    case ESPANOL:
                        fraseMotivacional = "Disfruta de la riqueza y pasión de la literatura en español.";
                        break;
                    case INGLES:
                        fraseMotivacional = "Explora los clásicos universales y mundos fascinantes en inglés.";
                        break;
                    case FRANCES:
                        fraseMotivacional = "Déjate envolver por la elegancia y el encanto de las letras francesas.";
                        break;
                    case PORTUGUES:
                        fraseMotivacional = "Siente la belleza y el alma vibrante de la literatura en portugués.";
                        break;
                    case ALEMAN:
                        fraseMotivacional = "Descubre la profundidad y genialidad de los grandes pensadores en alemán.";
                        break;
                    case ITALIANO:
                        fraseMotivacional = "Vive el arte, el romance y la historia a través de la literatura italiana.";
                        break;
                    default:
                        fraseMotivacional = "La literatura no conoce fronteras; viaja a través de este idioma.";
                }
            } else if (filtros.masPopulares() != null && !filtros.masPopulares().isEmpty()) {
                listadoLibros = libroService.listar100LibrosMasDescargados();
                textoResultado = "Top 100 Libros Más Populares";
                fraseMotivacional = "Las obras maestras que el mundo no deja de leer.";
            } else if (filtros.librosFavoritos() != null && !filtros.librosFavoritos().isEmpty()){
                listadoLibros = libroService.listarFavoritos();
                textoResultado = "Tus Libros Favoritos";
                fraseMotivacional = "Tus tesoros literarios más preciados en un solo lugar.";
            } else if (filtros.librosGuardados() != null && !filtros.librosGuardados().isEmpty()) {
                listadoLibros = libroService.listarLibros();
                textoResultado = "Todos tus libros guardados";
                fraseMotivacional = "Tu biblioteca personal, creciendo libro a libro.";
            } else if (filtros.inicial() != null && !filtros.inicial().isEmpty()) {
                listadoLibros = libroService.listarLibrosPorInicial(filtros.inicial());
                textoResultado = "Libros que comienzan con: " + filtros.inicial().toUpperCase();
                fraseMotivacional = "Letra por letra, explorando el alfabeto de la imaginación.";
            } else if (filtros.anio() != null) {
                listadoLibros = libroService.listarLibrosDeAutoresVivosPorAnio(filtros.anio());
                textoResultado = "Libros de autores vivos hasta: " + filtros.anio();
                fraseMotivacional = "Viajando en el tiempo para conectar con sus autores.";
            } else if (filtros.vibra() != null && !filtros.vibra().isEmpty()) {
                listadoLibros = libroService.listarLibroPorVibra(filtros.vibra());
                textoResultado = "Libros con vibra: #" + filtros.vibra();
                fraseMotivacional = "Explorando emociones y subgéneros únicos.";
            } else if (filtros.listarAutores() != null && !filtros.listarAutores().isEmpty()) {
                List<com.aluracursos.literalura.model.Autor> autoresTemp = new ArrayList<>(libroService.listarAutores());
                
                if (filtros.nombreAutorFiltro() != null && !filtros.nombreAutorFiltro().isBlank()) {
                    String filtro = quitarAcentos(filtros.nombreAutorFiltro());
                    autoresTemp = autoresTemp.stream()
                        .filter(a -> quitarAcentos(a.getNombre()).contains(filtro) || quitarAcentos(a.getNombreFormateado()).contains(filtro))
                        .collect(Collectors.toList());
                }
                
                if ("alfabetico".equals(filtros.ordenAutores())) {
                    autoresTemp.sort(java.util.Comparator.comparing(a -> a.getNombreFormateado()));
                } else {
                    autoresTemp.sort((a, b) -> Integer.compare(b.getLibros().size(), a.getLibros().size()));
                }
                
                listadoAutores = autoresTemp;
                
                buscandoLibros = false;
                textoResultado = "Autores registrados en la plataforma";
                fraseMotivacional = "Conoce a las mentes brillantes que dieron vida a estas historias.";
            } else if (filtros.librosEliminados() != null && !filtros.librosEliminados().isEmpty()) {
                listaEliminados = libroService.listarEliminados();
                buscandoLibros = false;
                textoResultado = "Papelera de reciclaje";
                fraseMotivacional = "Títulos descartados que esperan una segunda oportunidad.";
            } else {
                // Default search if nothing is specified
                isDefaultSearch = true;
            }
            
            if (!isDefaultSearch) {
                if (buscandoLibros && listadoLibros != null && filtros.librosEliminados() == null) {
                    textoResultado += " (" + listadoLibros.size() + " resultados)";
                } else if (!buscandoLibros && listadoAutores != null && !listadoAutores.isEmpty()) {
                    textoResultado += " (" + listadoAutores.size() + " resultados)";
                } else if (filtros.librosEliminados() != null && listaEliminados != null) {
                    textoResultado += " (" + listaEliminados.size() + " resultados)";
                }
            }
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("error", "Error procesando la búsqueda");
        }

        List<AutorDTO> listadoAutoresDTO = listadoAutores.stream().map(AutorDTO::fromEntity).collect(Collectors.toList());
        List<LibroDTO> listaEliminadosDTO = listaEliminados.stream().map(LibroDTO::fromEntity).collect(Collectors.toList());

        org.springframework.data.domain.Page<LibroDTO> paginaLibros;
        List<LibroDTO> paginatedList;
        int size = 12;

        if (isDefaultSearch) {
            org.springframework.data.domain.Page<Libro> dbPage = libroService.listarLibrosPaginados(org.springframework.data.domain.PageRequest.of(page, size));
            paginatedList = dbPage.getContent().stream().map(LibroDTO::fromEntity).collect(Collectors.toList());
            paginaLibros = new org.springframework.data.domain.PageImpl<>(paginatedList, org.springframework.data.domain.PageRequest.of(page, size), dbPage.getTotalElements());
        } else {
            // Mapeo a DTOs
            List<LibroDTO> listadoLibrosDTO = listadoLibros.stream().map(LibroDTO::fromEntity).collect(Collectors.toList());
            
            // Paginación en memoria
            int start = Math.min(page * size, listadoLibrosDTO.size());
            int end = Math.min((start + size), listadoLibrosDTO.size());
            
            paginatedList = listadoLibrosDTO.subList(start, end);
            paginaLibros = new org.springframework.data.domain.PageImpl<>(paginatedList, org.springframework.data.domain.PageRequest.of(page, size), listadoLibrosDTO.size());
        }

        model.addAttribute("paginaLibros", paginaLibros);
        model.addAttribute("listadoLibros", paginatedList); // mantenemos compatibilidad con frontend
        model.addAttribute("listadoAutores", listadoAutoresDTO);
        model.addAttribute("textoResultado", textoResultado);
        model.addAttribute("fraseMotivacional", fraseMotivacional);
        model.addAttribute("listaEliminados", listaEliminadosDTO);
        model.addAttribute("buscandoLibros", buscandoLibros);
        model.addAttribute("filtros", filtros);

        return "resultado.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id, @RequestHeader(value = "Referer", required = false) String referer) {
        Libro libro = libroService.obtenerPorId(id);
        if (libro != null) {
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

    @PostMapping("/resumen/{id}")
    public ResponseEntity<String> generarResumenIA(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean recrear) {
        try {
            Libro libro = libroService.obtenerPorId(id);
            if (libro != null) {
                // Si el libro ya tiene resumen guardado y NO estamos forzando recrearlo, lo devolvemos directo
                if (!recrear && libro.getResumen() != null && !libro.getResumen().isBlank()) {
                    return ResponseEntity.ok(libro.getResumen());
                }

                // Extraemos el autor
                String autorNombre = "Autor Desconocido";
                if (libro.getAutores() != null && !libro.getAutores().isEmpty()) {
                    autorNombre = libro.getAutores().get(0).getNombre();
                }

                // Llamamos a la magia de la IA
                String nuevoResumen = resumenIAService.obtenerResumen(libro.getTitulo(), autorNombre);

                // Lo guardamos en la base de datos de Postgres para futuras visitas
                libro.setResumen(nuevoResumen);
                libroRepo.save(libro);

                return ResponseEntity.ok(nuevoResumen);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el resumen.");
        }
    }

    private String quitarAcentos(String texto) {
        if (texto == null) return "";
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();
    }
}
