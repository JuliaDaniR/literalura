package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.enumerador.Lenguaje;
import com.aluracursos.literalura.model.Libro;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ILibroRepository  extends JpaRepository<Libro,Long> {

    @Query("SELECT f FROM Libro l JOIN l.formatos f WHERE l.id = :id")
    List<String> buscarFormatosDelLibro(@Param("id") Long id);
    List<Libro> findAllByEstadoTrue(); // Método para encontrar solo libros activos

    List<Libro> findAllByLenguajeAndEstadoTrue(Lenguaje lenguaje); // Método para encontrar libros por lenguaje y activos

    public List<Libro> findAllByFavoritoTrue();

    @Query("SELECT l FROM Libro l WHERE l.estado = true ORDER BY l.titulo")
    List<Libro> findTop10ByEstadoTrueOrderByTitulo(Pageable pageable);

    @Query("SELECT l FROM Libro l WHERE l.estado = true ORDER BY l.cantidadDescargas DESC")
    List<Libro> findTop10ByEstadoTrueOrderByCantidadDescargasDesc(Pageable pageable);

    @Query("SELECT l FROM Libro l WHERE l.estado = true AND l.lenguaje = :lenguaje ORDER BY l.cantidadDescargas DESC")
    List<Libro> findTop10ByLenguajeAndEstadoTrueOrderByCantidadDescargasDesc(@Param("lenguaje") Lenguaje lenguaje, Pageable pageable);

}
