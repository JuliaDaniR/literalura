# Literalura

Descripción breve de tu proyecto y su objetivo.
Este proyecto fue realizado para cumplir con el Challenge de Spring del programa de Alura con Oracle

## Características

- Busca libros en la API de Gutendex.
- Muestra detalles de libros, incluidos diferentes formatos disponibles.
- Descarga, ve en línea o escucha audiolibros.
- Métodos asincrónicos para consultas rápidas a la API.
- Recorrido completo de las más de 2900 páginas de la API.
- Puedes buscar a través de diferentes filtros.
- Tambien puedes eliminar libros para que ya no aparezcan en tus búsquedas
- Puedes guardar tus favoritos
## Entidades y sus Vinculaciones

### Libro

- **id**: Identificador único del libro.
- **titulo**: Título del libro.
- **cantidadDescargas**: Número de descargas del libro.
- **tipoDeMedio**: Tipo de medio del libro (por ejemplo, texto, audio).
- **autores**: Lista de autores del libro (relación muchos a muchos con la entidad `Autor`).
- **lenguaje**: Lenguaje en el que está escrito el libro.
- **categoria**: Categoría del libro.
- **formatos**: Lista de formatos disponibles para el libro (por ejemplo,Ver en linea, Descargar PDF, ePub , audiolibro).
- **imagen**: URL de la imagen de la portada del libro.
- **estado**: Permite dar de baja para poder ocultar los libros que no desea ver.

### Autor

- **id**: Identificador único del autor.
- **nombre**: Nombre del autor.
- **anioNac**: Año de nacimiento del autor.
- **anioMuerte**: Año de muerte del autor (si aplica).
- **Acceso a Biografia**: Los autores tienen un enlace a su biografia en wikipedia.

Las entidades `Libro` y `Autor` tienen una relación de muchos a muchos. Esto significa que un libro puede tener múltiples autores y un autor puede haber escrito múltiples libros. La relación se maneja mediante una tabla intermedia `autor_libro`.

## Tecnologías Utilizadas

 ![Postgres](https://img.shields.io/badge/Postgres-316192?style=flat&logo=postgresql&logoColor=white)
 ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)
 ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white)
 ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat&logo=thymeleaf&logoColor=white)
 ![HTML](https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=html5&logoColor=white)
 ![CSS](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=css3&logoColor=white)


## Ejemplos de Uso

## Instalación

1. Clona este repositorio.
2. Abre el proyecto en tu IDE preferido.
3. Configura las credenciales de la API de Gutendex en el archivo de propiedades (`application.properties` o `application.yml`).
4. Ejecuta la aplicación.

## Créditos

Este proyecto fue creado por Julia Daniela Rodriguez.
