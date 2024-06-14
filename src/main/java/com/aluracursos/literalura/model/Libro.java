package com.aluracursos.literalura.model;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "libros")
@Getter
@Setter
public class Libro {

    @Id
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column
    private Integer cantidadDescargas;

    @Column
    private String tipoDeMedio;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "autor_libro",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores;

    @Enumerated(EnumType.STRING)
    @Column(name = "lenguaje")
    private Lenguaje lenguaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private Categoria categoria;

    @ElementCollection
    @CollectionTable(name = "libro_formatos", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "formato")
    private List<String> formatos;

    private String imagen;
    
    private Boolean estado;
    
    private Boolean favorito;
    
    public Libro() {
        this.autores = new ArrayList<>();
    }

    public Libro(Long id, String titulo, Integer cantidadDescargas,
            String tipoDeMedio, List<Autor> autores, Lenguaje lenguaje,
            Categoria categoria, List<String> formatos, String imagen, Boolean estado , Boolean favorito) {
        this.id = id;
        this.titulo = titulo;
        this.cantidadDescargas = cantidadDescargas;
        this.tipoDeMedio = tipoDeMedio;
        this.autores = autores;
        this.lenguaje = lenguaje;
        this.categoria = categoria;
        this.formatos = formatos;
        this.imagen = imagen;
        this.estado = estado;
        this.favorito = favorito;
    }
    
    public boolean isFavorito() {
        return favorito;
    }

    @Override
    public String toString() {
        return "\n***************************************************"
                + "\nLibro{"
                + "\nTitulo:'" + titulo + '\''
                + "\nCantidad Descargas:" + cantidadDescargas
                + "\nAutores:" + autores
                + "\nLenguajes:" + lenguaje
                + "\nCategoria:" + categoria
                + "\nTipo De Medio:" + tipoDeMedio
                + "\n****************************************************";
    }
}
