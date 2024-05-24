package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Libro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ILibroRepository  extends JpaRepository<Libro,Long> {

    @Query("SELECT f FROM Libro l JOIN l.formatos f WHERE l.id = :id")
    List<String> buscarFormatosDelLibro(@Param("id") Long id);
}
