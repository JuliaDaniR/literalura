package com.aluracursos.literalura.service;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroEliminadoRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConversorAClaseLibroService {

    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final int cantidadMaximaPaginas = 2298;
    private final List<Libro> listadoLibros = new ArrayList<>();
    private Libro libro = new Libro();
    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;
    private final ILibroEliminadoRepository libroEliminadoRepo;

    private Categoria categoria;

    @Autowired
    public ConversorAClaseLibroService(ILibroRepository libroRepo, IAutorRepository autorRepo, ILibroEliminadoRepository libroEliminadoRepo) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
        this.libroEliminadoRepo = libroEliminadoRepo;
    }

    public List<Libro> consultaApi(String url) {
        var json = consumoApi.obtenerDatos(url);
        List<DatosLibro> listado = conversorGeneral(json);
        List<Libro> libros = new ArrayList<>();
        for (DatosLibro datosLibro : listado) {
            Libro libro = convertirDatosApiALibro(datosLibro);
            if (libro != null) {
                libros.add(libro);
            }
        }
        return libros;
    }

    List<Libro> consultaApiPorTema(String url, String tema) {
        System.out.println("url "+url);
        System.out.println("tema "+tema);
        var json = consumoApi.obtenerDatos(url);
        List<DatosLibro> listado = conversorGeneral(json);
        System.out.println("listado " + listado);
        List<Libro> libros = new ArrayList<>();
        for (DatosLibro datosLibro : listado) {
            Libro libro = convertirDatosApiALibroPorTema(datosLibro, tema);
            if (libro != null) {
                libros.add(libro);
                System.out.println("libros "+libro.getTitulo());
            }
        }
        return libros;
    }

    private List<DatosLibro> conversorGeneral(String json) {
        try {
            Datos datos = conversor.obtenerDatos(json, Datos.class);
            return datos.resultados();
        } catch (Exception e) {
            System.err.println("Error al convertir JSON a DatosLibro: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional
    private Libro convertirDatosApiALibro(DatosLibro datosLibro) {
        // Consultar la lista de libros eliminados
        List<LibrosEliminados> librosEliminados = libroEliminadoRepo.findAll();

        // Verificar si el libro está en la lista de libros eliminados
        boolean contiene = librosEliminados.stream()
                .anyMatch(l -> l.getLibroId().equals(datosLibro.id()));

        // Si el libro está en la lista de libros eliminados, salir del método
        if (contiene) {
            System.out.println("El libro está en la lista de libros eliminados. No se realizará ninguna acción.");
            return null;
        }

        // Buscar el libro en la base de datos
        Optional<Libro> libroEncontrado = libroRepo.findById(datosLibro.id());

        // Si el libro ya existe en la base de datos, devolverlo sin realizar más acciones
        if (libroEncontrado.isPresent()) {
            return libroEncontrado.get();
        }
        
        // Si no existe en la base crea uno nuevo
        Libro libro = new Libro();
        if (datosLibro != null) {
            libro.setId(datosLibro.id());
            // Truncar el título a 255 caracteres
            String titulo = datosLibro.titulo();
            if (titulo.length() > 255) {
                titulo = titulo.substring(0, 255);
            }
            libro.setTitulo(titulo);
            libro.setEstado(Boolean.TRUE);
            libro.setFavorito(Boolean.FALSE);
            libro.setCantidadDescargas(datosLibro.cantidadDescargas());
            libro.setTipoDeMedio(datosLibro.tipoDeMedio());
            if (datosLibro.lenguajes() != null && !datosLibro.lenguajes().isEmpty()) {
                Lenguaje enumLenguaje = null;
                for (String lenguaje : datosLibro.lenguajes()) {
                    enumLenguaje = Lenguaje.fromString(lenguaje);
                    libro.setLenguaje(enumLenguaje);
                }
                if (enumLenguaje != null) {
                } else {
                    libro.setLenguaje(Lenguaje.OTRO);
                }
            }
            if (datosLibro.temas() != null && !datosLibro.temas().isEmpty()) {
                Categoria categoria = null;
                for (String tema : datosLibro.temas()) {
                    String[] palabras = tema.split(" ");
                    String ultimaPalabra = palabras[palabras.length - 1];
                    for (Categoria posibleCategoria : Categoria.values()) {
                        if (ultimaPalabra.equalsIgnoreCase(posibleCategoria.getEnIngles())) {
                            categoria = posibleCategoria;
                            break;
                        }
                    }
                    if (categoria != null) {
                        break;
                    }
                }
                if (categoria != null) {
                    libro.setCategoria(categoria);
                } else {
                    libro.setCategoria(Categoria.OTRO);
                }
            }
            String urlImagen = "https://www.gutenberg.org/cache/epub/" + datosLibro.id() + "/pg" + datosLibro.id() + ".cover.medium.jpg";
            libro.setImagen(urlImagen);

            if (datosLibro.formatos() != null && !datosLibro.formatos().isEmpty()) {
                List<String> formatos = new ArrayList<>();
                for (Map.Entry<String, String> entry : datosLibro.formatos().entrySet()) {
                    formatos.add(entry.getKey());
                }
                libro.setFormatos(formatos);
            }

            // Procesar autores
            if (datosLibro.listaAutores() != null) {
                List<Autor> autores = new ArrayList<>();
                for (DatosAutor datoAutor : datosLibro.listaAutores()) {
                    Autor autorExistente = autorRepo.findByNombreAndAnioNacAndAnioMuerte(
                            datoAutor.nombre(), datoAutor.anioNacimiento(), datoAutor.anioMuerte());
                    if (autorExistente != null) {
                        autores.add(autorExistente); // Agrega el autor existente
                    } else {
                        Autor nuevoAutor = new Autor();
                        nuevoAutor.setNombre(datoAutor.nombre());
                        nuevoAutor.setAnioNac(datoAutor.anioNacimiento());
                        nuevoAutor.setAnioMuerte(datoAutor.anioMuerte());
                        autorRepo.save(nuevoAutor); // Guarda el nuevo autor
                        autores.add(nuevoAutor); // Agrega el nuevo autor
                    }
                }
                libro.setAutores(autores);
            } else {
                System.out.println("No se encontraron datos de autores para el libro.");
            }
            libroRepo.save(libro);
            return libro;
        } else {
            System.out.println("No se encontraron datos para el libro.");
            return null;
        }
    }

    @Transactional
    private Libro convertirDatosApiALibroPorTema(DatosLibro datosLibro, String tema) {
        // Consultar la lista de libros eliminados
        List<LibrosEliminados> librosEliminados = libroEliminadoRepo.findAll();

        // Verificar si el libro está en la lista de libros eliminados
        boolean contiene = librosEliminados.stream()
                .anyMatch(l -> l.getLibroId().equals(datosLibro.id()));

        // Si el libro está en la lista de libros eliminados, salir del método
        if (contiene) {
            System.out.println("El libro está en la lista de libros eliminados. No se realizará ninguna acción.");
            return null;
        }

        // Buscar el libro en la base de datos
        Optional<Libro> libroEncontrado = libroRepo.findById(datosLibro.id());

        // Si el libro ya existe en la base de datos, devolverlo sin realizar más acciones
        if (libroEncontrado.isPresent()) {
            return libroEncontrado.get();
        }

        // Crear un nuevo libro si no existe en la base de datos
        Libro libro = new Libro();
        if (datosLibro != null) {
            libro.setId(datosLibro.id());

            // Truncar el título a 255 caracteres
            String titulo = datosLibro.titulo();
            if (titulo.length() > 255) {
                titulo = titulo.substring(0, 255);
            }
            libro.setTitulo(titulo);
            libro.setEstado(Boolean.TRUE);
            libro.setCantidadDescargas(datosLibro.cantidadDescargas());
            libro.setTipoDeMedio(datosLibro.tipoDeMedio());

            if (datosLibro.lenguajes() != null && !datosLibro.lenguajes().isEmpty()) {
                Lenguaje enumLenguaje = null;
                for (String lenguaje : datosLibro.lenguajes()) {
                    enumLenguaje = Lenguaje.fromString(lenguaje);
                    libro.setLenguaje(enumLenguaje);
                }
                if (enumLenguaje == null) {
                    libro.setLenguaje(Lenguaje.OTRO);
                }
            }

            if (datosLibro.temas() != null && !datosLibro.temas().isEmpty()) {
                Categoria categoria = Categoria.fromEspanol(tema);
                libro.setCategoria(categoria);
            }

            String urlImagen = "https://www.gutenberg.org/cache/epub/" + datosLibro.id() + "/pg" + datosLibro.id() + ".cover.medium.jpg";
            libro.setImagen(urlImagen);

            if (datosLibro.formatos() != null && !datosLibro.formatos().isEmpty()) {
                List<String> formatos = new ArrayList<>();
                for (Map.Entry<String, String> entry : datosLibro.formatos().entrySet()) {
                    formatos.add(entry.getKey());
                }
                libro.setFormatos(formatos);
            }

            // Procesar autores
            if (datosLibro.listaAutores() != null) {
                List<Autor> autores = new ArrayList<>();
                for (DatosAutor datoAutor : datosLibro.listaAutores()) {
                    Autor autorExistente = autorRepo.findByNombreAndAnioNacAndAnioMuerte(
                            datoAutor.nombre(), datoAutor.anioNacimiento(), datoAutor.anioMuerte());
                    if (autorExistente != null) {
                        autores.add(autorExistente); // Agrega el autor existente
                    } else {
                        Autor nuevoAutor = new Autor();
                        nuevoAutor.setNombre(datoAutor.nombre());
                        nuevoAutor.setAnioNac(datoAutor.anioNacimiento());
                        nuevoAutor.setAnioMuerte(datoAutor.anioMuerte());
                        autorRepo.save(nuevoAutor); // Guarda el nuevo autor
                        autores.add(nuevoAutor); // Agrega el nuevo autor
                    }
                }
                libro.setAutores(autores);
            } else {
                System.out.println("No se encontraron datos de autores para el libro.");
            }

            libroRepo.save(libro);
            return libro;
        } else {
            System.out.println("No se encontraron datos para el libro.");
            return null;
        }
    }

}
