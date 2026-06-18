# 📚 Literalura - Tu Biblioteca Digital Mágica

**Literalura** es una plataforma web moderna y avanzada construida en Java y Spring Boot que te permite buscar, almacenar y descubrir libros de dominio público de la API de Gutendex. Originalmente concebido como un Challenge de consola para el programa Alura+Oracle, ¡este proyecto ha evolucionado a una aplicación web completa impulsada por Inteligencia Artificial!

## ✨ Características Principales

- **Búsqueda Inteligente:** Encuentra libros por nombre, autor, idioma, palabra clave o fecha.
- **🪄 Bibliotecario Virtual (Chatbot IA):** Un asistente mágico integrado en la página que utiliza IA para recomendarte lecturas personalizadas y encontrar libros específicos en segundos.
- **🧠 Análisis de "Vibras":** Cuando se guardan libros, una IA los analiza en segundo plano y les asigna etiquetas de "vibras" o subgéneros (ej. `#MisterioGótico`, `#RomanceTrágico`), permitiéndote filtrar por el sentimiento que evocan.
- **📝 Resúmenes Generados por IA:** ¿No estás seguro si leer un libro? Pídele a la IA que genere un pequeño resumen sin spoilers de cualquier obra directamente en su página de detalles.
- **Interfaz Moderna y Premium:** Diseño web responsivo utilizando CSS moderno con efectos de *glassmorphism*, animaciones fluidas, paleta de colores vibrante y modo oscuro de alta calidad.
- **Gestión Completa:** Guarda tus libros favoritos, envía libros a la papelera de reciclaje, restaura lecturas y accede a las biografías en Wikipedia de los autores.
- **⚡ Rendimiento Optimizado (N+1 & Paginación SQL):** Código interno pulido con soporte nativo de paginación en base de datos (`LIMIT/OFFSET`) y solución de problemas clásicos de JPA como el N+1 a través de `@FetchMode.SUBSELECT`.

## 🗄️ Entidades y Base de Datos

### Libro
- **id**: Identificador único.
- **titulo**: Título de la obra.
- **cantidadDescargas**: Número total de descargas globales.
- **tipoDeMedio**: Tipo de medio (ej: texto, audio).
- **autores**: Relación muchos-a-muchos con los creadores.
- **lenguaje**: Idioma de escritura.
- **categoria**: Categoría general del libro.
- **formatos**: Enlaces para leer en línea, descargar ePub, audiolibro, etc.
- **imagen**: Portada original del libro.
- **vibras**: Etiquetas emocionales generadas automáticamente por Inteligencia Artificial.
- **resumen**: Descripción generada por IA.
- **estado / favorito**: Permite marcar libros como favoritos o enviarlos a la papelera (soft delete).

### Autor
- **id**: Identificador único.
- **nombre**: Nombre del autor.
- **anioNac / anioMuerte**: Período de vida.
- **libros**: Relación de obras escritas.

## 🛠️ Tecnologías Utilizadas

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Postgres](https://img.shields.io/badge/Postgres-316192?style=flat&logo=postgresql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat&logo=thymeleaf&logoColor=white)
![HTML](https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=html5&logoColor=white)
![CSS](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=css3&logoColor=white)
![OpenAI/Gemini](https://img.shields.io/badge/AI_Powered-10A37F?style=flat&logo=openai&logoColor=white)

## 🚀 Instalación y Configuración

1. Clona este repositorio.
2. Abre el proyecto en tu IDE preferido (IntelliJ, Eclipse, VSCode).
3. Asegúrate de tener una base de datos **PostgreSQL** corriendo.
4. (Opcional) Configura tus API Keys de OpenAI / Gemini en tu entorno para que el Bibliotecario Virtual y los análisis de "Vibras" funcionen.
5. Ejecuta la clase principal `LiteraluraApplication.java`.
6. Abre tu navegador y dirígete a `http://localhost:8080`.

## 👩‍💻 Créditos

Este proyecto fue creado por **Julia Daniela Rodriguez** como parte del programa **ONE (Oracle Next Education)** en colaboración con **Alura Latam**.

