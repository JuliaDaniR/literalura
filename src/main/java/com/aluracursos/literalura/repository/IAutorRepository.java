package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IAutorRepository extends JpaRepository<Autor,Long> {

    Autor findByNombreAndAnioNacAndAnioMuerte(String nombre, Integer integer, Integer integer1);
    
    List<Autor> findByNombreContainingIgnoreCase(String nombreAutor);
    
    // Método para encontrar autores sin descripción para procesarlos con IA
    List<Autor> findTop10ByDescripcionIsNull();
    
    @Query("SELECT a FROM Autor a")
    List<Autor> findTop22Autores(Pageable pageable);
}
