# Literalura

Descripción breve de tu proyecto y su objetivo.
Este proyecto fue realizado para cumplir con el Challenge de Spring del programa de Alura con Oracle

## Características

- Busca libros en la API de Gutendex.
- Muestra detalles de libros, incluidos diferentes formatos disponibles.
- Descarga, ve en línea o escucha audiolibros.
- Métodos asincrónicos para consultas rápidas a la API.
- Recorrido completo de las más de 2900 páginas de la API.
## Entidades y sus Vinculaciones

### Libro

- **id**: Identificador único del libro.
- **titulo**: Título del libro.
- **cantidadDescargas**: Número de descargas del libro.
- **tipoDeMedio**: Tipo de medio del libro (por ejemplo, texto, audio).
- **autores**: Lista de autores del libro (relación muchos a muchos con la entidad `Autor`).
- **lenguaje**: Lenguaje en el que está escrito el libro.
- **categoria**: Categoría del libro.
- **formatos**: Lista de formatos disponibles para el libro (por ejemplo, PDF, ePub).
- **imagen**: URL de la imagen de la portada del libro.
- **estado**: Permite dar de baja para poder ocultar los libros que no desea ver.

### Autor

- **id**: Identificador único del autor.
- **nombre**: Nombre del autor.
- **anioNac**: Año de nacimiento del autor.
- **anioMuerte**: Año de muerte del autor (si aplica).
- **libros**: Lista de libros escritos por el autor (relación muchos a muchos con la entidad `Libro`).

Las entidades `Libro` y `Autor` tienen una relación de muchos a muchos. Esto significa que un libro puede tener múltiples autores y un autor puede haber escrito múltiples libros. La relación se maneja mediante una tabla intermedia `autor_libro`.

## Tecnologías Utilizadas

- Java
- Spring Boot
- Thymeleaf
- HTML
- CSS

## Ejemplos de Uso

```java
// Ejemplo de código para buscar libros en la API de Gutendex
LibroService libroService = new LibroService();
List<Libro> libros = libroService.buscarLibros("título del libro");
## Instalación

1. Clona este repositorio.
2. Abre el proyecto en tu IDE preferido.
3. Configura las credenciales de la API de Gutendex en el archivo de propiedades (`application.properties` o `application.yml`).
4. Ejecuta la aplicación.

## Contribuir

Las contribuciones son bienvenidas. Por favor, sigue estas instrucciones:

1. Haz un fork del proyecto.
2. Crea una nueva rama (`git checkout -b feature/nueva-caracteristica`).
3. Haz tus cambios y realiza un commit (`git commit -am 'Agrega nueva característica'`).
4. Haz push a la rama (`git push origin feature/nueva-caracteristica`).
5. Abre un Pull Request.

## Créditos

Este proyecto fue creado por Julia Daniela Rodriguez.
