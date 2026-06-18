package com.aluracursos.literalura.service;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;

@Service
public class LibroServiceAsync {

    private final String URL_BASE = "https://gutendex.com/books/";
    private final String PAGE = "?page=";
    final String NOMBRE = "?search=";
    final String LENGUAJE = "?languages=";
    final String PALABRA_CLAVE = "?topic=";
    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;
    private final ConversorAClaseLibroService conversorAClaseLibroService;
    private final ConsumoApi consumoApi;
    private Categoria categoria;

    @Value("${busqueda.enCurso}")
    private boolean busquedaEnCurso;

    @Value("${busqueda.cantidadResultado}")
    private Integer cantidadResultados;

    @Value("${busqueda.tipoBusqueda}")
    private String tipoBusqueda;

    @Value("${busqueda.cantidadResultadoParcial}")
    private Integer cantidadResultadosParcial;

    @Autowired
    public LibroServiceAsync(ILibroRepository libroRepo, IAutorRepository autorRepo, ConversorAClaseLibroService conversorAClaseLibroService, ConsumoApi consumoApi) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
        this.conversorAClaseLibroService = conversorAClaseLibroService;
        this.consumoApi = consumoApi;
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosPorNombre(String nombreLibro) {
        return CompletableFuture.supplyAsync(() -> {
            busquedaEnCurso = true;
            cantidadResultados = 0;
            List<Libro> lista = getDatosLibroPorNombre(nombreLibro);
            busquedaEnCurso = false;
            cantidadResultados = lista.size();
            tipoBusqueda = "Para la busqueda de: \nNombre del libro: " + nombreLibro.toUpperCase();
            return lista;
        });
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosMasDescargados() {
        return CompletableFuture.supplyAsync(() -> {
            busquedaEnCurso = true;
            cantidadResultados = 0;
            List<Libro> lista = getDatosLibroMasDescargados();
            busquedaEnCurso = false;
            cantidadResultados = lista.size();
            tipoBusqueda = "Para la busqueda de: \nLibros más descargados";
            return lista;
        });
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosPorLenguaje(String lenguajeLibro) {
        return CompletableFuture.supplyAsync(() -> {
            busquedaEnCurso = true;
            cantidadResultados = 0;
            List<Libro> lista = getDatosLibroPorLenguaje(lenguajeLibro);
            busquedaEnCurso = false;
            cantidadResultados = lista.size();
            Lenguaje leng = Lenguaje.fromString(lenguajeLibro);
            tipoBusqueda = "Para la busqueda de: \nLenguaje: " + leng;
            return lista;
        });
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosPorPalabraClave(String palabraClave) {
        return CompletableFuture.supplyAsync(() -> {
            busquedaEnCurso = true;
            cantidadResultados = 0;
            List<Libro> lista = getDatosLibroPorPalabraClave(palabraClave);
            busquedaEnCurso = false;
            cantidadResultados = lista.size();
            tipoBusqueda = "Para la busqueda de: \nPalabra Clave: " + palabraClave.toUpperCase();
            return lista;
        });
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosPorTema(String tema) {
        return CompletableFuture.supplyAsync(() -> {
            busquedaEnCurso = true;
            cantidadResultados = 0;
            List<Libro> lista = getDatosLibroPorTema(tema);
            busquedaEnCurso = false;
            cantidadResultados = lista.size();
            tipoBusqueda = "Para la busqueda de: \nCategoria: " + tema.toUpperCase();
            return lista;
        });
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosAutoresVivosPorAnio(Integer anio) {
        return CompletableFuture.supplyAsync(() -> {
            busquedaEnCurso = true;
            cantidadResultados = 0;
            List<Libro> lista = getDatosAutorVivoPorAño(anio);
            busquedaEnCurso = false;
            cantidadResultados = lista.size();
            tipoBusqueda = "Para la busqueda de: \nVivos hasta " + anio;
            return lista;
        });
    }

    public boolean isBusquedaEnCurso() {
        return busquedaEnCurso;
    }

    public int getCantidadResultados() {
        return cantidadResultados;
    }

    public int getCantidadResultadosParcial() {
        return cantidadResultadosParcial;
    }

    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    private List<Libro> procesarPaginacion(String urlInicial, boolean esPorTema, String temaNombre) {
        List<Libro> listado = new ArrayList<>();
        cantidadResultadosParcial = 0;
        String url = urlInicial;
        
        while (url != null) {
            List<Libro> lista;
            if (esPorTema) {
                lista = conversorAClaseLibroService.consultaApiPorTema(url, temaNombre);
            } else {
                lista = conversorAClaseLibroService.consultaApi(url);
            }
            listado.addAll(lista);
            url = obtenerSiguientePagina(url);
            cantidadResultadosParcial += lista.size();
        }
        System.out.println("Búsqueda finalizada");
        return listado;
    }

    public List<Libro> getDatosLibroPorNombre(String nombreLibro) {
        String url = URL_BASE + NOMBRE + nombreLibro.replace(" ", "+");
        return procesarPaginacion(url, false, null);
    }

    public List<Libro> getDatosLibroPorLenguaje(String lenguajeLibro) {
        String url = URL_BASE + LENGUAJE + lenguajeLibro;
        return procesarPaginacion(url, false, null);
    }

    public List<Libro> getDatosLibroPorPalabraClave(String palabraClave) {
        String url = URL_BASE + PALABRA_CLAVE + palabraClave.replace(" ", "+");
        return procesarPaginacion(url, false, null);
    }

    public List<Libro> getDatosLibroPorTema(String tema) {
        Categoria cate = Categoria.fromEspanol(tema);
        String temaIngles = cate.getEnIngles();
        String url = URL_BASE + PALABRA_CLAVE + temaIngles.replace(" ", "+");
        return procesarPaginacion(url, true, cate.name());
    }

    public List<Libro> getDatosLibroMasDescargados() {
        List<Libro> listado = new ArrayList<>();
        cantidadResultadosParcial = 0;
        for (int i = 1; i < 6; i++) {
            String url = "https://gutendex.com/books/?page=" + i + "&sort=downloads";
            List<Libro> lista = conversorAClaseLibroService.consultaApi(url);
            listado.addAll(lista);

            cantidadResultadosParcial = cantidadResultadosParcial + lista.size();
        }
        return listado;
    }

    public List<Libro> getDatos10LibroMasDescargados() {
        List<Libro> listado = new ArrayList<>();
        cantidadResultadosParcial = 0;
        for (int i = 1; i < 3; i++) {
            String url = "https://gutendex.com/books/?page=" + i + "&sort=downloads";
            busquedaEnCurso = true;
            cantidadResultados = 0;
            List<Libro> lista = conversorAClaseLibroService.consultaApi(url);
            listado.addAll(lista);
            
            cantidadResultadosParcial = cantidadResultadosParcial + lista.size();
            System.out.println("parcial "+cantidadResultadosParcial);
            cantidadResultados = cantidadResultadosParcial;
        }
        tipoBusqueda = "Aquí hay algunos de los Libros más descargados";
        busquedaEnCurso = false;

        return listado;
    }

    public List<Libro> getDatosAutorVivoPorAño(Integer anio) {
        String url = "https://gutendex.com/books/?author_year_end=" + anio;
        return procesarPaginacion(url, false, null);
    }

    // Este metodo me permite agilizar la busqueda en la api 
    // Porque busca en la pagina siguiente que hay resultados
    // a traves de next y no recorre todas que demoraria demasiado
    private String obtenerSiguientePagina(String urlActual) {
        try {
            String jsonResponse = consumoApi.obtenerDatos(urlActual);
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            if (jsonObject.has("next") && !jsonObject.get("next").isJsonNull()) {
                return jsonObject.get("next").getAsString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
