package com.aluracursos.literalura.service;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConversorAClaseLibroService {

    private final ConsumoApi consumoApi;
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;

    private Categoria categoria;

    @Autowired
    public ConversorAClaseLibroService(ILibroRepository libroRepo, IAutorRepository autorRepo, ConsumoApi consumoApi) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
        this.consumoApi = consumoApi;
    }

    public List<Libro> consultaApi(String url) {
        var json = consumoApi.obtenerDatos(url);
        List<DatosLibro> listado = conversorGeneral(json);
        List<Libro> libros = new ArrayList<>();
        for (DatosLibro datosLibro : listado) {
            Libro libro = procesarDatosLibro(datosLibro, null);
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
            Libro libro = procesarDatosLibro(datosLibro, tema);
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

    @Transactional
    private Libro procesarDatosLibro(DatosLibro datosLibro, String temaFijo) {
        if (datosLibro == null) {
            System.out.println("No se encontraron datos para el libro.");
            return null;
        }

        Optional<Libro> libroEncontrado = libroRepo.findById(datosLibro.id());
        if (libroEncontrado.isPresent()) {
            return libroEncontrado.get();
        }
        
        Libro libro = new Libro();
        libro.setId(datosLibro.id());
        
        String titulo = datosLibro.titulo();
        if (titulo != null && titulo.length() > 255) {
            titulo = titulo.substring(0, 255);
        }
        libro.setTitulo(titulo);
        libro.setEstado(Boolean.TRUE);
        libro.setFavorito(Boolean.FALSE);
        libro.setCantidadDescargas(datosLibro.cantidadDescargas());
        libro.setTipoDeMedio(datosLibro.tipoDeMedio());
        
        if (datosLibro.lenguajes() != null && !datosLibro.lenguajes().isEmpty()) {
            Lenguaje enumLenguaje = Lenguaje.fromString(datosLibro.lenguajes().get(0));
            libro.setLenguaje(enumLenguaje != null ? enumLenguaje : Lenguaje.OTRO);
        } else {
            libro.setLenguaje(Lenguaje.OTRO);
        }

        if (datosLibro.temas() != null && !datosLibro.temas().isEmpty()) {
            if (temaFijo != null) {
                libro.setCategoria(Categoria.fromEspanol(temaFijo));
            } else {
                Categoria categoriaEncontrada = null;
                for (String tema : datosLibro.temas()) {
                    String[] palabras = tema.split(" ");
                    String ultimaPalabra = palabras[palabras.length - 1];
                    for (Categoria posibleCategoria : Categoria.values()) {
                        if (ultimaPalabra.equalsIgnoreCase(posibleCategoria.getEnIngles())) {
                            categoriaEncontrada = posibleCategoria;
                            break;
                        }
                    }
                    if (categoriaEncontrada != null) break;
                }
                libro.setCategoria(categoriaEncontrada != null ? categoriaEncontrada : Categoria.OTRO);
            }
        } else {
            libro.setCategoria(Categoria.OTRO);
        }

        String urlImagen = "https://www.gutenberg.org/cache/epub/" + datosLibro.id() + "/pg" + datosLibro.id() + ".cover.medium.jpg";
        libro.setImagen(urlImagen);

        if (datosLibro.formatos() != null && !datosLibro.formatos().isEmpty()) {
            libro.setFormatos(new ArrayList<>(datosLibro.formatos().keySet()));
        }

        if (datosLibro.listaAutores() != null) {
            List<Autor> autores = new ArrayList<>();
            for (DatosAutor datoAutor : datosLibro.listaAutores()) {
                Autor autorExistente = autorRepo.findByNombreAndAnioNacAndAnioMuerte(
                        datoAutor.nombre(), datoAutor.anioNacimiento(), datoAutor.anioMuerte());
                if (autorExistente != null) {
                    autores.add(autorExistente);
                } else {
                    Autor nuevoAutor = new Autor();
                    nuevoAutor.setNombre(datoAutor.nombre());
                    nuevoAutor.setAnioNac(datoAutor.anioNacimiento());
                    nuevoAutor.setAnioMuerte(datoAutor.anioMuerte());
                    autorRepo.save(nuevoAutor);
                    autores.add(nuevoAutor);
                }
            }
            libro.setAutores(autores);
        }

        libroRepo.save(libro);
        return libro;
    }

}
