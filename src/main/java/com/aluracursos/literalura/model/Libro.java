package com.aluracursos.literalura.model;

import com.aluracursos.literalura.enumerador.Categoria;
import com.aluracursos.literalura.enumerador.Lenguaje;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

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

    public Libro() {
        this.autores = new ArrayList<>();
    }

    public Libro(Long id, String titulo, Integer cantidadDescargas,
            String tipoDeMedio, List<Autor> autores, Lenguaje lenguaje,
            Categoria categoria, List<String> formatos, String imagen) {
        this.id = id;
        this.titulo = titulo;
        this.cantidadDescargas = cantidadDescargas;
        this.tipoDeMedio = tipoDeMedio;
        this.autores = autores;
        this.lenguaje = lenguaje;
        this.categoria = categoria;
        this.formatos = formatos;
        this.imagen = imagen;
    }

    public Map<String, String> getFormatosConUrls() {
        Map<String, String> formatosConUrls = new LinkedHashMap<>();

        // Agregar los formatos según su importancia
        formatosConUrls.put("text/html", "https://www.gutenberg.org/ebooks/" + id + ".html.images");
        formatosConUrls.put("application/pdf", "https://www.gutenberg.org/ebooks/" + id + ".pdf");
        formatosConUrls.put("audio/mp4", "https://www.gutenberg.org/files/"+ id + "/m4b/" + id + "-01.m4b");
        formatosConUrls.put("application/epub+zip", "https://www.gutenberg.org/ebooks/" + id + ".epub3.images");
        formatosConUrls.put("application/octet-stream", "https://www.gutenberg.org/cache/epub/" + id + "/pg" + id + "-h.zip");

        return formatosConUrls.entrySet().stream()
                .limit(5) // Limitar a los 5 primeros elementos
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
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
