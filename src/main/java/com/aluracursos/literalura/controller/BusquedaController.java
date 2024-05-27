package com.aluracursos.literalura.controller;

import com.aluracursos.literalura.model.EstadoBusqueda;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.service.LibroServiceAsync;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BusquedaController {

    private final LibroServiceAsync libroServiceAsync;

    public BusquedaController(LibroServiceAsync libroServiceAsync) {
        this.libroServiceAsync = libroServiceAsync;
    }

    @GetMapping("/estadoBusqueda")
    public ResponseEntity<EstadoBusqueda> obtenerEstadoBusqueda() {
        boolean busquedaEnCurso = libroServiceAsync.isBusquedaEnCurso();
        int cantidadResultados = libroServiceAsync.getCantidadResultados();
        int cantidadResultadosParcial = libroServiceAsync.getCantidadResultadosParcial();
        String tipoBusqueda = libroServiceAsync.getTipoBusqueda();
        EstadoBusqueda estadoBusqueda = new EstadoBusqueda(busquedaEnCurso, cantidadResultados, cantidadResultadosParcial, tipoBusqueda);
        return ResponseEntity.ok(estadoBusqueda);
    }

    @GetMapping("/resultadosBusquedaPorNombre")
    public ResponseEntity<List<Libro>> obtenerResultadosBusquedaPorNombre(@RequestParam String nombre) {
        CompletableFuture<List<Libro>> future = libroServiceAsync.actualizarDatosLibrosPorNombre(nombre);
        return obtenerResultado(future);
    }

    @GetMapping("/resultadosBusquedaMasDescargados")
    public ResponseEntity<List<Libro>> obtenerResultadosBusquedaMasDescargados() {
        CompletableFuture<List<Libro>> future = libroServiceAsync.actualizarDatosLibrosMasDescargados();
        return obtenerResultado(future);
    }

    @GetMapping("/resultadosBusquedaPorLenguaje")
    public ResponseEntity<List<Libro>> obtenerResultadosBusquedaPorLenguaje(@RequestParam String lenguaje) {
        CompletableFuture<List<Libro>> future = libroServiceAsync.actualizarDatosLibrosPorLenguaje(lenguaje);
        return obtenerResultado(future);
    }

    @GetMapping("/resultadosBusquedaPorPalabraClave")
    public ResponseEntity<List<Libro>> obtenerResultadosBusquedaPorPalabraClave(@RequestParam String palabraClave) {
        CompletableFuture<List<Libro>> future = libroServiceAsync.actualizarDatosLibrosPorPalabraClave(palabraClave);
        return obtenerResultado(future);
    }

    @GetMapping("/resultadosBusquedaPorTema")
    public ResponseEntity<List<Libro>> obtenerResultadosBusquedaPorTema(@RequestParam String categoria) {
        CompletableFuture<List<Libro>> future = libroServiceAsync.actualizarDatosLibrosPorTema(categoria);
        return obtenerResultado(future);
    }

    @GetMapping("/resultadosBusquedaAutoresVivosPorAnio")
    public ResponseEntity<List<Libro>> obtenerResultadosBusquedaAutoresVivosPorAnio(@RequestParam Integer anio) {
        CompletableFuture<List<Libro>> future = libroServiceAsync.actualizarDatosAutoresVivosPorAnio(anio);
        return obtenerResultado(future);
    }

    private ResponseEntity<List<Libro>> obtenerResultado(CompletableFuture<List<Libro>> future) {
        List<Libro> resultados;
        try {
            resultados = future.get(); // Espera hasta que el resultado esté disponible
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Manejo de excepciones si es necesario
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(resultados);
    }
}
