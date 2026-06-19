package com.aluracursos.literalura.service;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class LibroService {

    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;
    private final LibroServiceAsync serviceAsync;
    private Categoria categoria;

    @Autowired
    public LibroService(ILibroRepository libroRepo, IAutorRepository autorRepo, LibroServiceAsync serviceAsync) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
        this.serviceAsync = serviceAsync;
    }

    public List<Libro> listarLibros() {
        List<Libro> libros = libroRepo.findAll();
        int cont = 0;
        for (Libro libro : libros) {
            if (libro.getCategoria() == null) {
                libro.setCategoria(Categoria.OTRO);
                System.out.println("cambiando...faltan" + cont);
                cont++;
            }
        }
        return libros;
    }

    public Page<Libro> listarLibrosPaginados(Pageable pageable) {
        return libroRepo.findAllByEstadoTrue(pageable);
    }

    public List<Libro> listarLibrosPorNombre(String nombreLibro) {
        List<Libro> listaPorNombre = libroRepo.findByTituloContainingIgnoreCaseAndEstadoTrue(nombreLibro);
        
        // Búsqueda flexible (Fuzzy)
        if (listaPorNombre.isEmpty()) {
            List<Libro> todosLosLibros = libroRepo.findAllByEstadoTrue();
            listaPorNombre = todosLosLibros.stream()
                .filter(libro -> esSimilitudAceptable(libro.getTitulo(), nombreLibro))
                .collect(Collectors.toList());
        }

        boolean mostrarCartel = listaPorNombre.isEmpty();
        serviceAsync.actualizarDatosLibrosPorNombre(nombreLibro, mostrarCartel);
        return listaPorNombre;
    }

    public List<Libro> listar100LibrosMasDescargados() {
        List<Libro> resultado = new ArrayList<>();
        List<Libro> libros = libroRepo.findAll();
        boolean mostrarCartel = libros.isEmpty();
        serviceAsync.actualizarDatosLibrosMasDescargados(mostrarCartel);
        if (!libros.isEmpty()) {
            resultado = libros.stream()
                    .sorted(Comparator.comparing(Libro::getCantidadDescargas).reversed())
                    .limit(100)
                    .collect(Collectors.toList());
        }
        return resultado;
    }
    
    
    public List<Libro> listar10LibrosMasDescargados() {

        List<Libro> resultado = new ArrayList<>();
        List<Libro> libros = libroRepo.findAll();
        boolean mostrarCartel = libros.isEmpty();
        serviceAsync.actualizarDatosLibrosMasDescargados(mostrarCartel);
        if (!libros.isEmpty()) {
            resultado = libros.stream()
                    .sorted(Comparator.comparing(Libro::getCantidadDescargas).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
        }
        return resultado;
    }

    public List<Libro> listarLibroPorTema(String tema) {
        categoria = Categoria.valueOf(tema.toUpperCase());
        List<Libro> listado = libroRepo.findByCategoriaAndEstadoTrue(categoria);

        boolean mostrarCartel = listado.isEmpty();
        serviceAsync.actualizarDatosLibrosPorTema(tema, mostrarCartel);
        return listado;
    }

    public List<Libro> listarLibroPorPalabraClave(String palabraClave) {

        List<Libro> lista = serviceAsync.getDatosLibroPorPalabraClave(palabraClave);
        List<Libro> libros = lista.stream()
                .collect(Collectors.toList());

        return libros;
    }

    public List<Libro> listarLibroPorIdioma(String eleccion) {
        Lenguaje lenguajeEnum = Lenguaje.fromEspanol(eleccion.toUpperCase());
        String lenguajeCode = lenguajeEnum.getCode();
        List<Libro> libros = libroRepo.findAllByLenguajeAndEstadoTrue(lenguajeEnum);

        boolean mostrarCartel = libros.isEmpty();
        serviceAsync.actualizarDatosLibrosPorLenguaje(lenguajeCode, mostrarCartel);
        return libros;
    }

    public List<Libro> listarLibrosPorInicial(String inicial) {
        return libroRepo.findByTituloStartingWithIgnoreCaseAndEstadoTrue(inicial);
    }

    public List<Libro> listarLibroPorVibra(String vibra) {
        return libroRepo.findByVibrasContainingIgnoreCaseAndEstadoTrue(vibra);
    }

    public List<Libro> listarLibroPorAutor(String autorIngresado) {
        List<Libro> librosPorAutor = libroRepo.findByNombreAutorContainingIgnoreCaseAndEstadoTrue(autorIngresado);
        
        // Búsqueda flexible (Fuzzy)
        if (librosPorAutor.isEmpty()) {
            List<Libro> todosLosLibros = libroRepo.findAllByEstadoTrue();
            librosPorAutor = todosLosLibros.stream()
                .filter(libro -> libro.getAutores().stream()
                    .anyMatch(a -> esSimilitudAceptable(a.getNombre(), autorIngresado) || esSimilitudAceptable(a.getNombreFormateado(), autorIngresado)))
                .collect(Collectors.toList());
        }

        boolean mostrarCartel = librosPorAutor.isEmpty();
        serviceAsync.actualizarDatosLibrosPorNombre(autorIngresado, mostrarCartel);
        return librosPorAutor;
    }

    public List<Autor> listarAutores() {
        return autorRepo.findAll();
    }

    public List<Libro> listarLibrosDeAutoresVivosPorAnio(Integer anio) {
        List<Libro> listado = serviceAsync.getDatosAutorVivoPorAño(anio);
        return listado;
    }

    public List<Libro> librosEnEspañol() {
        List<Libro> lista = libroRepo.findAll();
        return lista.stream()
                .filter(libro -> libro.getLenguaje() == Lenguaje.ESPANOL)
                .collect(Collectors.toList());
    }



    public Map<String, String> obtenerFormatos(Long id) {
        Libro libro = libroRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado"));

        Map<String, String> formatosConUrls = new LinkedHashMap<>();

        List<String> formatosDelLibro = libroRepo.buscarFormatosDelLibro(id);

        for (String formato : formatosDelLibro) {
            switch (formato) {
                case "text/html":
                    formatosConUrls.put("text/html", "https://www.gutenberg.org/ebooks/" + id + ".html.images");
                    break;
                case "application/pdf":
                    formatosConUrls.put("application/pdf", "https://www.gutenberg.org/ebooks/" + id + ".pdf");
                    break;
                case "audio/mp4":
                    formatosConUrls.put("audio/mp4", "https://www.gutenberg.org/files/" + id + "/m4b/" + id + "-01.m4b");
                    break;
                case "application/epub+zip":
                    formatosConUrls.put("application/epub+zip", "https://www.gutenberg.org/ebooks/" + id + ".epub3.images");
                    break;
                case "application/octet-stream":
                    formatosConUrls.put("application/octet-stream", "https://www.gutenberg.org/cache/epub/" + id + "/pg" + id + "-h.zip");
                    break;
                default:
                    // Manejar otros formatos si es necesario
                    break;
            }
        }
        return formatosConUrls.entrySet().stream()
                .limit(5) // Limitar a los 5 primeros elementos
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    public List<Libro> listarEliminados() {
        return libroRepo.findAllByEstadoFalse();
    }

    public void eliminarLibro(Long id) {
        Optional<Libro> libro = libroRepo.findById(id);
        if (libro.isPresent()) {
            Libro libroEntidad = libro.get();
            libroEntidad.setEstado(false);
            libroRepo.save(libroEntidad); // Guardar el cambio de estado en la base de datos
        }
    }

    public List<Libro> getLibrosPorIds(List<Long> ids) {
        List<Libro> listado = new ArrayList<>();
        for (Long id : ids) {
            Libro libro = obtenerPorId(id);
            listado.add(libro);
        }
        System.out.println("listado getLibro " + listado);
        return listado;
    }

    public Libro obtenerPorId(Long id) {
        Optional<Libro> libro = libroRepo.findById(id);
        System.out.println(" libro " + libro.get());
        return libro.orElse(null); // Manejar el caso cuando el libro no se encuentra
    }

    // --- Algoritmos de Búsqueda Flexible (Levenshtein) ---
    private boolean esSimilitudAceptable(String textoOriginal, String busqueda) {
        if (textoOriginal == null || busqueda == null) return false;
        String t = textoOriginal.toLowerCase();
        String b = busqueda.toLowerCase().trim();
        
        if (t.contains(b)) return true;

        int maxErroresPermitidos = b.length() <= 4 ? 1 : (b.length() <= 8 ? 2 : 3);
        
        // Compara texto completo
        if (calcularDistanciaLevenshtein(t, b) <= maxErroresPermitidos) return true;
        
        // Si el usuario busca una sola palabra, buscar coincidencia en cualquier palabra del título/autor
        String[] palabras = t.split("\\s+");
        String[] busquedaPalabras = b.split("\\s+");
        if (busquedaPalabras.length == 1) {
            for (String palabra : palabras) {
                if (calcularDistanciaLevenshtein(palabra, b) <= maxErroresPermitidos) return true;
            }
        } else {
            // Si busca múltiples palabras, coincidir si alguna palabra significativa (>3 letras) coincide
            for (String palabraBuscada : busquedaPalabras) {
                if (palabraBuscada.length() > 3) {
                    for (String palabraTitulo : palabras) {
                        if (calcularDistanciaLevenshtein(palabraTitulo, palabraBuscada) <= 1) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }

    private int calcularDistanciaLevenshtein(CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        for (int i = 0; i < len0; i++) cost[i] = i;

        for (int j = 1; j < len1; j++) {
            newcost[0] = j;
            for (int i = 1; i < len0; i++) {
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }
            int[] swap = cost; cost = newcost; newcost = swap;
        }
        return cost[len0 - 1];
    }

    public void restaurarLibro(Long id) {
        Optional<Libro> libro = libroRepo.findById(id);
        if (libro.isPresent()) {
            Libro libroEntidad = libro.get();
            libroEntidad.setEstado(true);
            libroRepo.save(libroEntidad);
        }
    }

    public List<Libro> listarFavoritos() {
        List<Libro> favoritos = libroRepo.findAllByFavoritoTrue();
        return favoritos != null ? favoritos : new ArrayList();
    }
    
    @org.springframework.cache.annotation.Cacheable("top10Libros")
    public List<Libro> findTop10Libros() {
        Pageable top10Pageable = PageRequest.of(0, 10);
        Page<Libro> page = (Page<Libro>) libroRepo.findTop10ByEstadoTrueOrderByCantidadDescargasDesc(top10Pageable);
        return page.getContent();
    }

    @org.springframework.cache.annotation.Cacheable("top10MasDescargados")
    public List<Libro> findTop10MasDescargados() {
        Pageable top10Pageable = PageRequest.of(0, 10);
      Page<Libro> page = (Page<Libro>) libroRepo.findTop10ByEstadoTrueOrderByCantidadDescargasDesc(top10Pageable);
        return page.getContent();
    }

    @org.springframework.cache.annotation.Cacheable("top10LibrosEspanol")
    public List<Libro> findTop10LibrosEspanol() {
        Pageable top10Pageable = PageRequest.of(0, 10);
       Page<Libro> page = (Page<Libro>) libroRepo.findTop10ByLenguajeAndEstadoTrueOrderByCantidadDescargasDesc(Lenguaje.ESPANOL, top10Pageable);
        return page.getContent();
    }
    
     @org.springframework.cache.annotation.Cacheable("top22Autores")
    public List<Autor> listarAutores(Pageable pageable) {

        return autorRepo.findTop22Autores(pageable);
    }
}
