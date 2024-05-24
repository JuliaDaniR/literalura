package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.LibrosEliminados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILibroEliminadoRepository extends JpaRepository<LibrosEliminados, Long>{
    
}
