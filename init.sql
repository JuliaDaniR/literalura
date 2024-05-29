-- init.sql
DELETE FROM libros;
DELETE FROM autores;
CREATE INDEX idx_libro_estado ON libro(estado);
CREATE INDEX idx_libro_titulo ON libro(titulo);
CREATE INDEX idx_libro_cantidad_descargas ON libro(cantidad_descargas);
CREATE INDEX idx_libro_lenguaje ON libro(lenguaje);
