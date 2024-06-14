package com.aluracursos.literalura.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
@Getter
@Setter
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String nombre;

    private Integer anioNac;

    private Integer anioMuerte;

    @ManyToMany(mappedBy = "autores", fetch = FetchType.EAGER)
    private List<Libro> libros = new ArrayList<>();
    
    public Autor() {
    }

    public Autor(Long id, String nombre, Integer anioNac, Integer anioMuerte) {
        this.id = id;
        this.nombre = nombre;
        this.anioNac = anioNac;
        this.anioMuerte = anioMuerte;
    }

    public Autor(String nombre, Integer anioNac, Integer anioMuerte) {
        this.nombre = nombre;
        this.anioNac = anioNac;
        this.anioMuerte = anioMuerte;
    }

    @Override
    public String toString() {
        return
                "\nNombre:'" + nombre + '\'' +
                "\nAño de Nacimiento:" + anioNac +
                "\nAño de Muerte:" + anioMuerte;
    }
}
