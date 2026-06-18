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
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    private List<Autor> autores;

    @Enumerated(EnumType.STRING)
    @Column(name = "lenguaje")
    private Lenguaje lenguaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private Categoria categoria;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "libro_formatos", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "formato")
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    private List<String> formatos;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "libro_vibras", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "vibra")
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    private List<String> vibras;

    @Column(columnDefinition = "TEXT")
    private String resumen;

    private String imagen;
    
    private Boolean estado;
    
    private Boolean favorito;
    
    public Libro() {
        this.autores = new ArrayList<>();
        this.vibras = new ArrayList<>();
    }

    public Libro(Long id, String titulo, Integer cantidadDescargas,
            String tipoDeMedio, List<Autor> autores, Lenguaje lenguaje,
            Categoria categoria, List<String> formatos, String imagen, Boolean estado , Boolean favorito, List<String> vibras) {
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
        this.vibras = vibras != null ? vibras : new ArrayList<>();
    }
    
    public boolean isFavorito() {
        return favorito != null && favorito;
    }
    
    public boolean isEstado() {
        return estado != null && estado;
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
