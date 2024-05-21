package com.aluracursos.literalura.service;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
    private Categoria categoria;

    @Autowired
    public LibroServiceAsync(ILibroRepository libroRepo, IAutorRepository autorRepo, ConversorAClaseLibroService conversorAClaseLibroService) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
        this.conversorAClaseLibroService = conversorAClaseLibroService;
    }

    @Async
    public void actualizarDatosLibrosPorNombre(String nombreLibro) {
        List<Libro> lista = getDatosLibroPorNombre(nombreLibro);
    }

    @Async
    public void actualizarDatosLibrosMasDescargados() {
        List<Libro> lista = getDatosLibroMasDescargados();
    }

    @Async
    public void actualizarDatosLibrosPorLenguaje(String lenguajeLibro) {
        List<Libro> lista = getDatosLibroPorLenguaje(lenguajeLibro);
    }

    @Async
    public void actualizarDatosLibrosPorPalabraClave(String palabraClave) {
        List<Libro> lista = getDatosLibroPorPalabraClave(palabraClave);
    }

    @Async
    public void actualizarDatosLibrosPorTema(String tema) {
        List<Libro> lista = getDatosLibroPorTema(tema);
    }

    @Async
    public void actualizarDatosAutoresVivosPorAnio(Integer anio) {
        List<Libro> lista = getDatosAutorVivoPorAño(anio);
    }

    public List<Libro> getDatosLibroPorNombre(String nombreLibro) {
        String url = URL_BASE + NOMBRE + nombreLibro.replace(" ", "+");
        List<Libro> listado = new ArrayList<>();
        while (url != null) {
            List<Libro> lista = conversorAClaseLibroService.consultaApi(url);
            listado.addAll(lista);

            // Obtener el enlace a la siguiente página de resultados
            url = obtenerSiguientePagina(url);
        }
        System.out.println("busqueda finalizada");
        return listado;
    }

    public List<Libro> getDatosLibroPorLenguaje(String lenguajeLibro) {
        String url = URL_BASE + LENGUAJE + lenguajeLibro;
        List<Libro> listado = new ArrayList<>();
        while (url != null) {
            List<Libro> lista = conversorAClaseLibroService.consultaApi(url);
            listado.addAll(lista);

            // Obtener el enlace a la siguiente página de resultados
            url = obtenerSiguientePagina(url);
        }
        System.out.println("busqueda finalizada");
        return listado;
    }

    public List<Libro> getDatosLibroPorPalabraClave(String palabraClave) {
        String url = URL_BASE + PALABRA_CLAVE + palabraClave.replace(" ", "+");
        List<Libro> listado = new ArrayList<>();
        while (url != null) {
            List<Libro> lista = conversorAClaseLibroService.consultaApi(url);
            listado.addAll(lista);
            url = obtenerSiguientePagina(url);
        }
        System.out.println("busqueda finalizada");
        return listado;
    }

    public List<Libro> getDatosLibroPorTema(String tema) {
        Categoria cate = Categoria.fromEspanol(tema);
        tema = cate.getEnIngles();
        String url = URL_BASE + PALABRA_CLAVE + tema.replace(" ", "+");
        List<Libro> listado = new ArrayList<>();
        while (url != null) {
            List<Libro> lista = conversorAClaseLibroService.consultaApiPorTema(url, cate.name());
            listado.addAll(lista);
            url = obtenerSiguientePagina(url);
        }
        System.out.println("busqueda finalizada");
        return listado;
    }

    public List<Libro> getDatosLibroMasDescargados() {
        List<Libro> listado = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            String url = "https://gutendex.com/books/?page=" + i + "&sort=downloads";
            List<Libro> lista = conversorAClaseLibroService.consultaApi(url);
            listado.addAll(lista);
        }
        return listado;
    }

    public List<Libro> getDatosAutorVivoPorAño(Integer anio) {
        String url = "https://gutendex.com/books/?author_year_end=" + anio;
        List<Libro> listado = new ArrayList<>();
        while (url != null) {
            List<Libro> lista = conversorAClaseLibroService.consultaApi(url);
            listado.addAll(lista);
            url = obtenerSiguientePagina(url);
        }
        System.out.println("busqueda finalizada");
        return listado;
    }

    private String obtenerSiguientePagina(String urlActual) {
        try {
            // Realiza una solicitud HTTP GET a la URL actual
            URL url = new URL(urlActual);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Lee la respuesta
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Analiza la respuesta JSON para obtener el enlace a la siguiente página
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            if (jsonResponse.has("next") && !jsonResponse.get("next").isJsonNull()) {
                return jsonResponse.get("next").getAsString();
            } else {
                return null;
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
