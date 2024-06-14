package com.aluracursos.literalura.service;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroEliminadoRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import jakarta.persistence.Cacheable;
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
    private final ILibroEliminadoRepository libroElimRepo;
    private final LibroServiceAsync serviceAsync;
    private Categoria categoria;

    @Autowired
    public LibroService(ILibroRepository libroRepo, IAutorRepository autorRepo, ILibroEliminadoRepository libroElimRepo, LibroServiceAsync serviceAsync) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
        this.libroElimRepo = libroElimRepo;
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

    public List<Libro> listarLibrosPorNombre(String nombreLibro) {

        List<Libro> listaPorNombre = new ArrayList<>();
        List<Libro> listado = libroRepo.findAll();
        if (listado.isEmpty()) {
            serviceAsync.getDatosLibroPorNombre(nombreLibro);
            listaPorNombre = listado.stream()
                    .filter(l -> l.getTitulo().toLowerCase().contains(nombreLibro.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            listaPorNombre = listado.stream()
                    .filter(l -> l.getTitulo().toLowerCase().contains(nombreLibro.toLowerCase()))
                    .collect(Collectors.toList());
            if (listaPorNombre.isEmpty()) {
                System.out.println("No se encontro ningun libro buscaremos en la api");
            }
        }
        serviceAsync.actualizarDatosLibrosPorNombre(nombreLibro);
        return listaPorNombre;
    }

    public List<Libro> listar100LibrosMasDescargados() {

        List<Libro> resultado = new ArrayList<>();
        List<Libro> libros = libroRepo.findAll();
        if (libros.isEmpty()) {
            serviceAsync.getDatosLibroMasDescargados();
            resultado = libros.stream()
                    .sorted(Comparator.comparing(Libro::getCantidadDescargas).reversed())
                    .limit(100)
                    .collect(Collectors.toList());
        } else {
            resultado = libros.stream()
                    .sorted(Comparator.comparing(Libro::getCantidadDescargas).reversed())
                    .limit(100)
                    .collect(Collectors.toList());
            if (resultado.isEmpty()) {
                System.out.println("No se encontro ningun libro buscaremos en la api");
            }
        }
        serviceAsync.actualizarDatosLibrosMasDescargados();
        return resultado;
    }
    
    
    public List<Libro> listar10LibrosMasDescargados() {

        List<Libro> resultado = new ArrayList<>();
        List<Libro> libros = libroRepo.findAll();
        if (libros.isEmpty()) {
            serviceAsync.getDatos10LibroMasDescargados();
            resultado = libros.stream()
                    .sorted(Comparator.comparing(Libro::getCantidadDescargas).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
        } else {
            resultado = libros.stream()
                    .sorted(Comparator.comparing(Libro::getCantidadDescargas).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
            if (resultado.isEmpty()) {
                System.out.println("No se encontro ningun libro buscaremos en la api");
            }
        }
        return resultado;
    }

    public List<Libro> listarLibroPorTema(String tema) {

        categoria = Categoria.valueOf(tema.toUpperCase());
        List<Libro> listado = new ArrayList<>();

        List<Libro> libros = libroRepo.findAll();
        if (libros.isEmpty()) {
            serviceAsync.getDatosLibroPorTema(tema);
            listado = libros.stream()
                    .filter(l -> l.getCategoria() == categoria)
                    .collect(Collectors.toList());
        } else {
            listado = libros.stream()
                    .filter(l -> l.getCategoria() == categoria)
                    .collect(Collectors.toList());

            if (listado.isEmpty()) {
                System.out.println("No se encontro ningun libro buscaremos en la api");
            }
        }
        serviceAsync.actualizarDatosLibrosPorTema(tema);
        return listado;
    }

    public List<Libro> listarLibroPorPalabraClave(String palabraClave) {

        List<Libro> lista = serviceAsync.getDatosLibroPorPalabraClave(palabraClave);
        List<Libro> libros = lista.stream()
                .collect(Collectors.toList());

        return libros;
    }

    public List<Libro> listarLibroPorIdioma(String eleccion) {

        List<Libro> libros = new ArrayList<>();
        var lenguaje = Lenguaje.fromEspanol(eleccion.toUpperCase()).getCode();
        System.out.println("*********from espanol" + lenguaje);

        List<Libro> list = libroRepo.findAll();
        if (list.isEmpty()) {
            serviceAsync.getDatosLibroPorLenguaje(lenguaje);
            var leng = Lenguaje.fromString(lenguaje.toUpperCase()).name();
            System.out.println("Leng from string " + leng);
            libros = list.stream()
                    .filter(l -> leng.equalsIgnoreCase(l.getLenguaje().name()))
                    .collect(Collectors.toList());
        } else {
            var leng = Lenguaje.fromString(lenguaje.toUpperCase()).name();
            System.out.println("Leng from string " + leng);
            libros = list.stream()
                    .filter(l -> leng.equalsIgnoreCase(l.getLenguaje().name()))
                    .collect(Collectors.toList());

            if (libros.isEmpty()) {
                System.out.println("No se encontro ningun libro buscaremos en la api");
            }
        }
        serviceAsync.actualizarDatosLibrosPorLenguaje(lenguaje);
        return libros;
    }

    public List<Libro> listarLibrosPorInicial(String inicial) {

        List<Libro> list = libroRepo.findAll();
        List<Libro> nuevaList = list.stream()
                .filter(l -> l.getTitulo().charAt(0) == inicial.toUpperCase().charAt(0)) // Comparar con la inicial ingresada
                .collect(Collectors.toList());

        return nuevaList;
    }

    public List<Libro> listarLibroPorAutor(String autorIngresado) {

        List<Libro> librosPorAutor = new ArrayList<>();
        List<Libro> libros = libroRepo.findAll();
        if (libros.isEmpty()) {
            serviceAsync.getDatosLibroPorNombre(autorIngresado);
            librosPorAutor = libros.stream()
                    .filter(libro -> libro.getAutores().stream()
                    .anyMatch(a -> a.getNombre().trim().toUpperCase().contains(autorIngresado.trim().toUpperCase())))// Filtrar libros que contienen al autor
                    .collect(Collectors.toList());
        } else {
            librosPorAutor = libros.stream()
                    .filter(libro -> libro.getAutores().stream()
                    .anyMatch(a -> a.getNombre().trim().toUpperCase().contains(autorIngresado.trim().toUpperCase())))// Filtrar libros que contienen al autor
                    .collect(Collectors.toList());

            if (librosPorAutor.isEmpty()) {
                System.out.println("No se encontro ningun libro buscaremos en la api buscaremos vuelva luego");
            }
        }
        serviceAsync.actualizarDatosLibrosPorNombre(autorIngresado);
        return librosPorAutor;
    }

    public List<Autor> listarAutores() {

        List<Autor> autores = autorRepo.findAll();
        for (Autor autor : autores) {
            String nuevoNombre = procesarNombreAutor(autor.getNombre());
            autor.setNombre(nuevoNombre);
        }
        return autores;
    }

    //Proceso el nombre del autor que viene desde la api para poder hacer la 
    //busqueda de su biografia en wikipedia
    private String procesarNombreAutor(String nombreAutor) {
        StringBuilder nombreAutorFormateado = new StringBuilder();
        String[] partes = nombreAutor.split(", ");
        if (partes.length == 2) {
            // Invertir el orden de las partes
            String nombre = partes[1];
            String apellido = partes[0];
            // Eliminar espacios en blanco adicionales
            nombre = nombre.trim();
            apellido = apellido.trim();
            // Eliminar palabras entre paréntesis en el nombre
            nombre = eliminarPalabrasEntreParentesis(nombre);
            // Formatear el nombre en el formato requerido
            nombreAutorFormateado.append(nombre).append("_").append(apellido);
        } else {
            // Si no hay una coma, no se puede invertir, así que se agrega el nombre original
            nombreAutorFormateado.append(nombreAutor);
        }
        return nombreAutorFormateado.toString();
    }

    //Elimino las palabras entre parentesis para el formato del nombre del autor
    private String eliminarPalabrasEntreParentesis(String texto) {
        StringBuilder textoSinParentesis = new StringBuilder();
        boolean dentroDeParentesis = false;
        for (char c : texto.toCharArray()) {
            if (c == '(') {
                dentroDeParentesis = true;
            } else if (c == ')') {
                dentroDeParentesis = false;
            } else if (!dentroDeParentesis) {
                textoSinParentesis.append(c);
            }
        }
        return textoSinParentesis.toString().trim();
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

    public void mostrarListaEliminados() {
        List<Libro> librosEliminados = new ArrayList<>();

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

    public List<LibrosEliminados> listarEliminados() {
        return libroElimRepo.findAll();
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

    public void restaurarLibro(Long id) {
        Optional<Libro> libro = libroRepo.findById(id);
        if (libro.isPresent()) {
            Libro libroEntidad = libro.get();
            libroEntidad.setEstado(true);
            libroRepo.save(libroEntidad); // Guardar el cambio de estado en la base de datos

            // Eliminar el registro de LibrosEliminados
            List<LibrosEliminados> lista = libroElimRepo.findAll();
            for (LibrosEliminados librosEliminados : lista) {
                if (librosEliminados.getLibroId().equals(id)) {
                    libroElimRepo.deleteById(librosEliminados.getId());
                    break;
                }
            }
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
