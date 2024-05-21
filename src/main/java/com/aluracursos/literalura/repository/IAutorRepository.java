package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAutorRepository extends JpaRepository<Autor,Long> {

    Autor findByNombreAndAnioNacAndAnioMuerte(String nombre, Integer integer, Integer integer1);
}
