package com.aluracursos.literalura.service;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
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

    private Categoria categoria;

    @Autowired
    public ConversorAClaseLibroService(ILibroRepository libroRepo, IAutorRepository autorRepo) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
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
        var json = consumoApi.obtenerDatos(url);
        List<DatosLibro> listado = conversorGeneral(json);
        List<Libro> libros = new ArrayList<>();
        for (DatosLibro datosLibro : listado) {
            Libro libro = convertirDatosApiALibroPorTema(datosLibro, tema);
            if (libro != null) {
                libros.add(libro);
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

    private Libro convertirDatosApiALibro(DatosLibro datosLibro) {
        Optional<Libro> libroEncontrado = libroRepo.findById(datosLibro.id());

        if (!libroEncontrado.isPresent()) {
            Libro libro = new Libro();
            if (datosLibro != null) {
                libro.setId(datosLibro.id());
                // Truncar el título a 255 caracteres
                String titulo = datosLibro.titulo();
                if (titulo.length() > 255) {
                    titulo = titulo.substring(0, 255);
                }
                libro.setTitulo(titulo);
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
        } else {
            Libro li = libroEncontrado.get();
            if (li.getCategoria() == null) {
                li.setCategoria(Categoria.OTRO);
            }
            return libroEncontrado.get();
        }
    }

    private Libro convertirDatosApiALibroPorTema(DatosLibro datosLibro, String tema) {
        Optional<Libro> libroEncontrado = libroRepo.findById(datosLibro.id());

        if (!libroEncontrado.isPresent()) {
            Libro libro = new Libro();
            if (datosLibro != null) {
                libro.setId(datosLibro.id());
                // Truncar el título a 255 caracteres
                String titulo = datosLibro.titulo();
                if (titulo.length() > 255) {
                    titulo = titulo.substring(0, 255);
                }
                libro.setTitulo(titulo);
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
                    categoria = Categoria.valueOf(tema);
                    libro.setCategoria(categoria);
                }
                
                String urlImagen = "https://www.gutenberg.org/cache/epub/" + datosLibro.id() + "/pg" + datosLibro.id() + ".cover.medium.jpg";
                libro.setImagen(urlImagen);

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
        } else {
            return libroEncontrado.get();
        }
    }
}
