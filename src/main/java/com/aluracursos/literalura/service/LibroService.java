package com.aluracursos.literalura.service;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<Libro> listarLibrosPorNombre(String nombreLibro) {

        List<Libro> listaPorNombre = new ArrayList<>();
        List<Libro> listado = libroRepo.findAll();
        if (listado.isEmpty()) {
            serviceAsync.getDatosLibroPorNombre(nombreLibro);
            listaPorNombre = listado.stream()
                    .filter(l -> l.getTitulo().equalsIgnoreCase(nombreLibro))
                    .collect(Collectors.toList());
        } else {
            listaPorNombre = listado.stream()
                    .filter(l -> l.getTitulo().equalsIgnoreCase(nombreLibro))
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

    public Libro obtenerPorId(Long id) {
        Libro libro1 = new Libro();
        Optional<Libro> libro = libroRepo.findById(id);
        if (libro.isPresent()) {
            libro1 = libro.get();
        }
        return libro1;
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
}
