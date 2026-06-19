package com.aluracursos.literalura.service;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.EstadoBusqueda;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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

    private final ConcurrentHashMap<String, EstadoBusqueda> busquedasActivas = new ConcurrentHashMap<>();
    private Map<String, Long> ultimasActualizaciones = new ConcurrentHashMap<>();

    @Autowired
    public LibroServiceAsync(ILibroRepository libroRepo, IAutorRepository autorRepo, ConversorAClaseLibroService conversorAClaseLibroService, ConsumoApi consumoApi) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
        this.conversorAClaseLibroService = conversorAClaseLibroService;
        this.consumoApi = consumoApi;
    }

    public Collection<EstadoBusqueda> getBusquedasActivas() {
        return busquedasActivas.values();
    }

    private boolean isActualizadoRecientemente(String tipoBusqueda) {
        Long lastTime = ultimasActualizaciones.get(tipoBusqueda);
        return lastTime != null && (System.currentTimeMillis() - lastTime) < 300000; // 5 minutos de cooldown
    }

    private boolean isBusquedaDuplicada(String tipoBusqueda) {
        return busquedasActivas.values().stream()
                .anyMatch(e -> e.isBusquedaEnCurso() && e.getTipoBusqueda().equals(tipoBusqueda));
    }

    private void programarLimpieza(String id) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(15000); // Mantener el cartel finalizado por 15 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            busquedasActivas.remove(id);
        });
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosPorNombre(String nombreLibro, boolean mostrarCartel) {
        String tipoBusq = "Para la busqueda de: \nNombre del libro: " + nombreLibro.toUpperCase();
        String id = null;
        EstadoBusqueda estado = null;
        
        synchronized (busquedasActivas) {
            if (isBusquedaDuplicada(tipoBusq) || isActualizadoRecientemente(tipoBusq)) return CompletableFuture.completedFuture(new ArrayList<>());
            if (mostrarCartel) {
                id = UUID.randomUUID().toString();
                estado = new EstadoBusqueda(id, true, 0, 0, tipoBusq);
                busquedasActivas.put(id, estado);
            }
        }
        
        List<Libro> lista = getDatosLibroPorNombre(nombreLibro, estado);
        
        if (estado != null) {
            estado.setCantidadResultados(lista.size());
            estado.setBusquedaEnCurso(false);
            programarLimpieza(id);
        }
        ultimasActualizaciones.put(tipoBusq, System.currentTimeMillis());
        return CompletableFuture.completedFuture(lista);
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosMasDescargados(boolean mostrarCartel) {
        String tipoBusq = "Para la busqueda de: \nLibros más descargados";
        String id = null;
        EstadoBusqueda estado = null;
        
        synchronized (busquedasActivas) {
            if (isBusquedaDuplicada(tipoBusq) || isActualizadoRecientemente(tipoBusq)) return CompletableFuture.completedFuture(new ArrayList<>());
            if (mostrarCartel) {
                id = UUID.randomUUID().toString();
                estado = new EstadoBusqueda(id, true, 0, 0, tipoBusq);
                busquedasActivas.put(id, estado);
            }
        }
        
        List<Libro> lista = getDatosLibroMasDescargados(estado);
        
        if (estado != null) {
            estado.setCantidadResultados(lista.size());
            estado.setBusquedaEnCurso(false);
            programarLimpieza(id);
        }
        ultimasActualizaciones.put(tipoBusq, System.currentTimeMillis());
        return CompletableFuture.completedFuture(lista);
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosPorLenguaje(String lenguajeLibro, boolean mostrarCartel) {
        Lenguaje leng = Lenguaje.fromString(lenguajeLibro);
        String tipoBusq = "Para la busqueda de: \nLenguaje: " + leng;
        String id = null;
        EstadoBusqueda estado = null;
        
        synchronized (busquedasActivas) {
            if (isBusquedaDuplicada(tipoBusq) || isActualizadoRecientemente(tipoBusq)) return CompletableFuture.completedFuture(new ArrayList<>());
            if (mostrarCartel) {
                id = UUID.randomUUID().toString();
                estado = new EstadoBusqueda(id, true, 0, 0, tipoBusq);
                busquedasActivas.put(id, estado);
            }
        }
        
        List<Libro> lista = getDatosLibroPorLenguaje(lenguajeLibro, estado);
        
        if (estado != null) {
            estado.setCantidadResultados(lista.size());
            estado.setBusquedaEnCurso(false);
            programarLimpieza(id);
        }
        ultimasActualizaciones.put(tipoBusq, System.currentTimeMillis());
        return CompletableFuture.completedFuture(lista);
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosPorPalabraClave(String palabraClave, boolean mostrarCartel) {
        String tipoBusq = "Para la busqueda de: \nPalabra Clave: " + palabraClave.toUpperCase();
        String id = null;
        EstadoBusqueda estado = null;
        
        synchronized (busquedasActivas) {
            if (isBusquedaDuplicada(tipoBusq) || isActualizadoRecientemente(tipoBusq)) return CompletableFuture.completedFuture(new ArrayList<>());
            if (mostrarCartel) {
                id = UUID.randomUUID().toString();
                estado = new EstadoBusqueda(id, true, 0, 0, tipoBusq);
                busquedasActivas.put(id, estado);
            }
        }
        
        List<Libro> lista = getDatosLibroPorPalabraClave(palabraClave, estado);
        
        if (estado != null) {
            estado.setCantidadResultados(lista.size());
            estado.setBusquedaEnCurso(false);
            programarLimpieza(id);
        }
        ultimasActualizaciones.put(tipoBusq, System.currentTimeMillis());
        return CompletableFuture.completedFuture(lista);
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosLibrosPorTema(String tema, boolean mostrarCartel) {
        String tipoBusq = "Para la busqueda de: \nCategoria: " + tema.toUpperCase();
        String id = null;
        EstadoBusqueda estado = null;
        
        synchronized (busquedasActivas) {
            if (isBusquedaDuplicada(tipoBusq) || isActualizadoRecientemente(tipoBusq)) return CompletableFuture.completedFuture(new ArrayList<>());
            if (mostrarCartel) {
                id = UUID.randomUUID().toString();
                estado = new EstadoBusqueda(id, true, 0, 0, tipoBusq);
                busquedasActivas.put(id, estado);
            }
        }
        
        List<Libro> lista = getDatosLibroPorTema(tema, estado);
        
        if (estado != null) {
            estado.setCantidadResultados(lista.size());
            estado.setBusquedaEnCurso(false);
            programarLimpieza(id);
        }
        ultimasActualizaciones.put(tipoBusq, System.currentTimeMillis());
        return CompletableFuture.completedFuture(lista);
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Libro>> actualizarDatosAutoresVivosPorAnio(Integer anio, boolean mostrarCartel) {
        String tipoBusq = "Para la busqueda de: \nAutor Vivo por Año: " + anio;
        String id = null;
        EstadoBusqueda estado = null;
        
        synchronized (busquedasActivas) {
            if (isBusquedaDuplicada(tipoBusq) || isActualizadoRecientemente(tipoBusq)) return CompletableFuture.completedFuture(new ArrayList<>());
            if (mostrarCartel) {
                id = UUID.randomUUID().toString();
                estado = new EstadoBusqueda(id, true, 0, 0, tipoBusq);
                busquedasActivas.put(id, estado);
            }
        }
        
        List<Libro> lista = getDatosAutorVivoPorAño(anio, estado);
        
        if (estado != null) {
            estado.setCantidadResultados(lista.size());
            estado.setBusquedaEnCurso(false);
            programarLimpieza(id);
        }
        ultimasActualizaciones.put(tipoBusq, System.currentTimeMillis());
        return CompletableFuture.completedFuture(lista);
    }

    private List<Libro> procesarPaginacion(String urlInicial, boolean esPorTema, String temaNombre, EstadoBusqueda estado) {
        List<Libro> listado = new ArrayList<>();
        if (estado != null) estado.setCantidadResultadosParcial(0);
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
            if (estado != null) estado.setCantidadResultadosParcial(estado.getCantidadResultadosParcial() + lista.size());
        }
        if (estado != null) System.out.println("Búsqueda finalizada: " + estado.getTipoBusqueda());
        return listado;
    }

    public List<Libro> getDatosLibroPorNombre(String nombreLibro) {
        return getDatosLibroPorNombre(nombreLibro, null);
    }

    public List<Libro> getDatosLibroPorNombre(String nombreLibro, EstadoBusqueda estado) {
        String url = URL_BASE + NOMBRE + nombreLibro.replace(" ", "+");
        return procesarPaginacion(url, false, null, estado);
    }

    public List<Libro> getDatosLibroPorLenguaje(String lenguajeLibro) {
        return getDatosLibroPorLenguaje(lenguajeLibro, null);
    }

    public List<Libro> getDatosLibroPorLenguaje(String lenguajeLibro, EstadoBusqueda estado) {
        String url = URL_BASE + LENGUAJE + lenguajeLibro;
        return procesarPaginacion(url, false, null, estado);
    }

    public List<Libro> getDatosLibroPorPalabraClave(String palabraClave) {
        return getDatosLibroPorPalabraClave(palabraClave, null);
    }

    public List<Libro> getDatosLibroPorPalabraClave(String palabraClave, EstadoBusqueda estado) {
        String url = URL_BASE + PALABRA_CLAVE + palabraClave.replace(" ", "+");
        return procesarPaginacion(url, false, null, estado);
    }

    public List<Libro> getDatosLibroPorTema(String tema) {
        return getDatosLibroPorTema(tema, null);
    }

    public List<Libro> getDatosLibroPorTema(String tema, EstadoBusqueda estado) {
        Categoria cate = Categoria.fromEspanol(tema);
        String temaIngles = cate.getEnIngles();
        String url = URL_BASE + PALABRA_CLAVE + temaIngles.replace(" ", "+");
        return procesarPaginacion(url, true, cate.name(), estado);
    }

    public List<Libro> getDatosLibroMasDescargados() {
        return getDatosLibroMasDescargados(null);
    }

    public List<Libro> getDatosLibroMasDescargados(EstadoBusqueda estado) {
        List<Libro> listado = new ArrayList<>();
        if (estado != null) estado.setCantidadResultadosParcial(0);
        for (int i = 1; i < 5; i++) { // 4 páginas = 128 libros
            String url = "https://gutendex.com/books/?page=" + i + "&sort=downloads";
            List<Libro> lista = conversorAClaseLibroService.consultaApi(url);
            
            for (Libro libro : lista) {
                if (listado.size() < 100) {
                    listado.add(libro);
                }
            }
            
            if (estado != null) estado.setCantidadResultadosParcial(listado.size());
            if (listado.size() >= 100) break;
        }
        return listado;
    }

    public List<Libro> getDatos10LibroMasDescargados() {
        return getDatos10LibroMasDescargados(null);
    }

    public List<Libro> getDatos10LibroMasDescargados(EstadoBusqueda estado) {
        List<Libro> listado = new ArrayList<>();
        if (estado != null) estado.setCantidadResultadosParcial(0);
        for (int i = 1; i < 2; i++) { // 1 página = 32 libros
            String url = "https://gutendex.com/books/?page=" + i + "&sort=downloads";
            List<Libro> lista = conversorAClaseLibroService.consultaApi(url);
            
            for (Libro libro : lista) {
                if (listado.size() < 10) {
                    listado.add(libro);
                }
            }
            
            if (estado != null) estado.setCantidadResultadosParcial(listado.size());
            if (listado.size() >= 10) break;
        }
        return listado;
    }

    public List<Libro> getDatosAutorVivoPorAño(Integer anio) {
        return getDatosAutorVivoPorAño(anio, null);
    }

    public List<Libro> getDatosAutorVivoPorAño(Integer anio, EstadoBusqueda estado) {
        String url = "https://gutendex.com/books/?author_year_end=" + anio;
        return procesarPaginacion(url, false, null, estado);
    }

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
