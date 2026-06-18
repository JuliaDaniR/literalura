package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.enumerador.Categoria;
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
    org.springframework.data.domain.Page<Libro> findAllByEstadoTrue(Pageable pageable);

    List<Libro> findAllByLenguajeAndEstadoTrue(Lenguaje lenguaje); // Método para encontrar libros por lenguaje y activos

    public List<Libro> findAllByFavoritoTrue();

    @Query("SELECT l FROM Libro l WHERE l.estado = true ORDER BY l.titulo")
    List<Libro> findTop10ByEstadoTrueOrderByTitulo(Pageable pageable);

    @Query("SELECT l FROM Libro l WHERE l.estado = true ORDER BY l.cantidadDescargas DESC")
    List<Libro> findTop10ByEstadoTrueOrderByCantidadDescargasDesc(Pageable pageable);

    @Query("SELECT l FROM Libro l WHERE l.estado = true ORDER BY l.cantidadDescargas DESC")
    List<Libro> findTop10ByLenguajeAndEstadoTrueOrderByCantidadDescargasDesc(@Param("lenguaje") Lenguaje lenguaje, Pageable pageable);

    // Nuevos métodos para Fase 1
    List<Libro> findAllByEstadoFalse();
    
    List<Libro> findByTituloContainingIgnoreCaseAndEstadoTrue(String titulo);
    
    List<Libro> findByCategoriaAndEstadoTrue(Categoria categoria);
    
    @Query("SELECT l FROM Libro l JOIN l.autores a WHERE UPPER(a.nombre) LIKE UPPER(CONCAT('%', :nombreAutor, '%')) AND l.estado = true")
    List<Libro> findByNombreAutorContainingIgnoreCaseAndEstadoTrue(@Param("nombreAutor") String nombreAutor);
    
    @Query("SELECT l FROM Libro l WHERE UPPER(SUBSTRING(l.titulo, 1, 1)) = UPPER(:inicial) AND l.estado = true")
    List<Libro> findByTituloStartingWithIgnoreCaseAndEstadoTrue(@Param("inicial") String inicial);
    
    @Query("SELECT DISTINCT l FROM Libro l JOIN l.vibras v WHERE UPPER(v) LIKE UPPER(CONCAT('%', :vibra, '%')) AND l.estado = true")
    List<Libro> findByVibrasContainingIgnoreCaseAndEstadoTrue(@Param("vibra") String vibra);
    
    @Query("SELECT l FROM Libro l WHERE l.vibras IS EMPTY AND l.estado = true")
    List<Libro> findLibrosSinVibras(org.springframework.data.domain.Pageable pageable);
    
}
