# 📚 Literalura — Biblioteca Digital con IA

[![Live Demo](https://img.shields.io/badge/🚀_Demo_en_vivo-Render-46E3B7?style=flat&logo=render&logoColor=white)](https://literalura-1-k9fk.onrender.com/)

**Literalura** es una aplicación web construida con **Java 21** y **Spring Boot 3.2.5** que te permite buscar, guardar y descubrir libros de dominio público desde la API de [Gutendex](https://gutendex.com/). Nació como un Challenge de consola para el programa **Alura + Oracle ONE** y evolucionó a una plataforma web completa con funcionalidades de Inteligencia Artificial.

---

## ✨ Funcionalidades

### 🔍 Búsqueda y Descubrimiento
- Buscar libros en la API de Gutendex por **nombre**, **autor**, **idioma**, **tema/categoría**, **palabra clave** o **año** de vida del autor.
- Listar el **Top 100 libros más descargados** de la plataforma.
- Filtrar libros guardados por **letra inicial** o por sus **vibras** emocionales.
- Ver listado de **autores registrados**, con filtro por nombre y ordenamiento alfabético o por cantidad de obras.

### 🗂️ Gestión de la Biblioteca Personal
- **Guardar** libros desde Gutendex a la base de datos local (PostgreSQL).
- **Soft delete**: enviar libros a la papelera de reciclaje (`estado = false`) y **restaurarlos** en cualquier momento.
- **Favoritos**: marcar/desmarcar libros como favoritos desde cualquier vista.
- **Paginación nativa en BD** (`LIMIT/OFFSET` vía Spring Data) en la vista por defecto y paginación en memoria para búsquedas filtradas.

### 🤖 Inteligencia Artificial (Cascada Dual)
El servicio [`ResumenIAService`](src/main/java/com/aluracursos/literalura/service/ResumenIAService.java) implementa una **cascada de proveedores** con reintentos automáticos:
1. **OpenRouter** (prioridad): modelos gratuitos configurables vía `openrouter.models`.
2. **Google Gemini** (fallback): modelos configurables vía `gemini.models`.

Esto alimenta tres características:

| Característica | Descripción |
|---|---|
| **Vibras** | Al guardar un libro, la IA genera 3 etiquetas de subgénero/emoción (ej. `Misterio Gótico`) en segundo plano via `@Async`. |
| **Resumen IA** | Genera (y cachea en BD) un resumen literario enriquecido en HTML del libro bajo demanda, con opción de regenerarlo. |
| **Bibliotecario Virtual (Chatbot)** | Chatbot en la UI que recomienda libros de Gutendex basándose en preferencias del usuario, devolviendo una respuesta JSON estructurada. |
| **Bio de Autores** | Descripción corta del autor generada por IA en una sola oración. |

### ⚡ Rendimiento
- **Virtual Threads de Java 21** habilitados (`spring.threads.virtual.enabled=true`) para alta concurrencia asíncrona.
- **Búsquedas asíncronas** con `CompletableFuture` (`LibroServiceAsync`) para no bloquear el hilo principal mientras se consulta Gutendex.
- **HikariCP** configurado con pool de 20 conexiones.
- **`@FetchMode.SUBSELECT`** en colecciones JPA para evitar el problema N+1.
- **Spring Cache** habilitado (`spring-boot-starter-cache`).

---

## 🗄️ Modelo de Datos

### Libro (`libros`)
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | PK (proveniente de Gutendex) |
| `titulo` | `String` | Título de la obra |
| `cantidadDescargas` | `Integer` | Total de descargas globales |
| `tipoDeMedio` | `String` | Tipo de medio (ej. texto) |
| `autores` | `List<Autor>` | Relación `@ManyToMany` |
| `lenguaje` | `Lenguaje` (enum) | Idioma del libro |
| `categoria` | `Categoria` (enum) | Categoría/género |
| `formatos` | `List<String>` | URLs de descarga/lectura en línea |
| `imagen` | `String` | URL de la portada |
| `vibras` | `List<String>` | Etiquetas generadas por IA |
| `resumen` | `TEXT` | Resumen generado por IA (cacheado) |
| `estado` | `Boolean` | `true` = activo, `false` = en papelera |
| `favorito` | `Boolean` | Marcado como favorito |

### Autor (`autores`)
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | PK (autoincremental) |
| `nombre` | `String` | Nombre en formato "Apellido, Nombre" |
| `anioNac` | `Integer` | Año de nacimiento |
| `anioMuerte` | `Integer` | Año de muerte |
| `descripcion` | `String` | Bio corta generada por IA |
| `libros` | `List<Libro>` | Relación inversa `@ManyToMany` |

### Enumeradores
- **`Lenguaje`**: `INGLES`, `ALEMAN`, `FRANCES`, `ESPANOL`, `ITALIANO`, `PORTUGUES`, `LATIN`, `RUSO`, `CHINO`, `JAPONES`, y otros (17 idiomas).
- **`Categoria`**: `FICCION`, `FANTASIA`, `MISTERIO`, `THRILLER`, `ROMANCE`, `BIOGRAFIA`, `HISTORIA`, `CIENCIA`, `FILOSOFIA`, `POESIA`, `DRAMA`, y otros (31 categorías).

---

## 🏗️ Arquitectura y Estructura del Proyecto

```
literalura/
├── src/main/java/com/aluracursos/literalura/
│   ├── LiteraluraApplication.java        # Clase principal
│   ├── config/                           # Configuraciones de Spring (Async, Cache)
│   ├── controller/
│   │   ├── LibroController.java          # CRUD web + endpoints IA (resumen, favorito)
│   │   ├── BusquedaController.java       # Endpoints de búsqueda asíncrona en Gutendex
│   │   ├── ChatbotController.java        # Endpoint REST del Bibliotecario Virtual
│   │   └── PortalController.java         # Controlador de la página principal (index)
│   ├── dto/                              # LibroDTO, AutorDTO, BusquedaRequestDTO
│   ├── enumerador/                       # Lenguaje.java, Categoria.java
│   ├── exception/                        # Manejo global de excepciones
│   ├── model/
│   │   ├── Libro.java                    # Entidad JPA principal
│   │   ├── Autor.java                    # Entidad JPA de autores
│   │   ├── EstadoBusqueda.java           # Estado de búsquedas asíncronas en curso
│   │   ├── Datos.java / DatosLibro.java / DatosAutor.java  # DTOs de Gutendex
│   │   └── Utilidades.java
│   ├── repository/
│   │   └── ILibroRepository.java         # JPA Repository con queries personalizadas
│   └── service/
│       ├── LibroService.java             # Lógica de negocio principal
│       ├── LibroServiceAsync.java        # Búsquedas asíncronas + vibras
│       ├── ResumenIAService.java         # Cascada IA: OpenRouter → Gemini
│       ├── ChatbotService.java           # Lógica del Bibliotecario Virtual
│       ├── ConsumoApi.java               # Cliente HTTP para Gutendex
│       ├── ConversorAClaseLibroService.java  # Mapeo de respuesta Gutendex → Libro
│       ├── ConvierteDatos.java           # Deserialización JSON (Gson)
│       └── ProcesadorVibrasTask.java     # Tarea asíncrona de generación de vibras
├── src/main/resources/
│   ├── application.properties            # Configuración completa de la app
│   ├── templates/                        # Vistas Thymeleaf
│   │   ├── index.html                    # Página principal / portal
│   │   ├── resultado.html                # Listado y filtros de libros/autores
│   │   ├── detalles.html                 # Detalle de libro con resumen IA
│   │   ├── error.html                    # Página de error
│   │   └── fragments/                   # Fragmentos reutilizables (navbar, footer)
│   └── static/                          # CSS, JS, imágenes
├── Dockerfile                            # Build multi-etapa (Maven → JRE Alpine)
├── docker-compose.yml                    # App + PostgreSQL
├── init.sql                              # Script de inicialización de la BD
└── pom.xml
```

---

## 🛠️ Stack Tecnológico

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2.5-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat&logo=thymeleaf&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=flat&logo=lombok&logoColor=white)
![Gson](https://img.shields.io/badge/Gson-4285F4?style=flat&logo=google&logoColor=white)

| Dependencia | Uso |
|---|---|
| `spring-boot-starter-web` | Controladores MVC y REST |
| `spring-boot-starter-data-jpa` | ORM con Hibernate |
| `spring-boot-starter-thymeleaf` | Motor de plantillas HTML |
| `spring-boot-starter-cache` | Abstracción de caché |
| `postgresql` | Driver JDBC |
| `lombok` | Reducción de boilerplate |
| `gson` | Deserialización de respuestas JSON de Gutendex |
| `spring-boot-devtools` | Recarga en caliente durante desarrollo |

**APIs externas:**
- [Gutendex](https://gutendex.com/) — catálogo de libros de dominio público (Project Gutenberg).
- [OpenRouter](https://openrouter.ai/) — proxy de modelos LLM gratuitos.
- [Google Gemini](https://ai.google.dev/) — fallback de IA.

---

## 🚀 Instalación y Configuración

### Opción A: Ejecución con Docker Compose (Recomendado)

```bash
# 1. Clonar el repositorio
git clone <url-del-repo>
cd literalura

# 2. Crear el archivo .env con las claves de IA (opcional, para funciones de IA)
echo "OPENROUTER_API_KEY=tu_clave_aqui" > .env
echo "GEMINI_API_KEY=tu_clave_aqui" >> .env

# 3. Levantar la app y la base de datos
docker-compose up --build
```

La aplicación estará disponible en `http://localhost:8080`.

> **Nota:** Las credenciales de BD configuradas en `docker-compose.yml` son `postgres` / `root`. Cámbialas si vas a exponer el servicio.

### Opción B: Ejecución Local (IDE)

**Requisitos previos:**
- Java 21+
- Maven 3.9+
- PostgreSQL corriendo localmente

**Pasos:**
1. Clonar el repositorio y abrir en IntelliJ IDEA / VSCode.
2. Configurar las variables de entorno necesarias (ver sección de configuración).
3. Ejecutar `LiteraluraApplication.java`.
4. Abrir `http://localhost:8080`.

### Variables de Entorno / Configuración

| Variable | Descripción | Requerida |
|---|---|---|
| `DB_URL` | URL JDBC de PostgreSQL (ej. `jdbc:postgresql://localhost:5432/literalura`) | ✅ Sí |
| `DB_USER` | Usuario de la base de datos | ✅ Sí |
| `DB_PASSWORD` | Contraseña de la base de datos | ✅ Sí |
| `OPENROUTER_API_KEY` | API Key de OpenRouter para funciones IA | ⚠️ Opcional |
| `GEMINI_API_KEY` | API Key de Google Gemini para funciones IA | ⚠️ Opcional |

> Si las claves de IA no se configuran, la app funciona completamente para búsqueda y gestión; sólo las funciones de vibras, resúmenes y chatbot quedarán inactivas.

---

## 📡 Endpoints Principales

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Página principal |
| `GET` | `/libros/listar` | Listado con filtros (nombre, autor, idioma, categoría, vibra, año, etc.) |
| `GET` | `/libros/detalles/{id}` | Detalle de un libro |
| `POST` | `/libros/resumen/{id}` | Genera (o regenera) el resumen IA del libro |
| `POST` | `/libros/favorito/{id}` | Toggle de favorito |
| `GET` | `/libros/eliminar/{id}` | Soft-delete (mover a papelera) |
| `GET` | `/libros/restaurar/{id}` | Restaurar desde papelera |
| `GET` | `/estadoBusqueda` | Estado de búsquedas asíncronas en curso |
| `GET` | `/resultadosBusquedaPorNombre?nombre=...` | Busca y guarda libros en Gutendex por nombre |
| `GET` | `/resultadosBusquedaMasDescargados` | Busca y guarda el top más descargado |
| `GET` | `/resultadosBusquedaPorLenguaje?lenguaje=...` | Busca por idioma en Gutendex |
| `GET` | `/resultadosBusquedaPorPalabraClave?palabraClave=...` | Busca por palabra clave en Gutendex |
| `GET` | `/resultadosBusquedaPorTema?categoria=...` | Busca por tema/categoría en Gutendex |
| `GET` | `/resultadosBusquedaAutoresVivosPorAnio?anio=...` | Busca autores vivos hasta un año |

---

## 🌐 Demo en Vivo

La aplicación está desplegada en **Render** y disponible públicamente:

🔗 **[https://literalura-1-k9fk.onrender.com/](https://literalura-1-k9fk.onrender.com/)**

> **Nota:** Al estar en el plan gratuito de Render, el servidor puede tardar unos segundos en despertar si estuvo inactivo.

---

## 👩‍💻 Créditos

Este proyecto fue creado por **Julia Daniela Rodriguez** como parte del programa **ONE (Oracle Next Education)** en colaboración con **Alura Latam**.
