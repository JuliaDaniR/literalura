//package com.aluracursos.literalura.Principal;
//
//import com.aluracursos.literalura.enumerador.Lenguaje;
//import com.aluracursos.literalura.repository.IAutorRepository;
//import com.aluracursos.literalura.repository.ILibroRepository;
//import com.aluracursos.literalura.service.ConversorAClaseLibroService;
//import com.aluracursos.literalura.service.LibroService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Scanner;
//
//@Component
//public class Principal {
//    @Autowired
//    private ILibroRepository libroRepo;
//    @Autowired
//    private IAutorRepository autorRepo;
//
//    @Autowired
//    private ConversorAClaseLibroService conversorAClaseLibroService;
//    private final LibroService libroService;
//
//    @Autowired
//    public Principal(ILibroRepository libroRepo, IAutorRepository autorRepo, ConversorAClaseLibroService conversorAClaseLibroService) {
//        this.libroRepo = libroRepo;
//        this.autorRepo = autorRepo;
//        this.conversorAClaseLibroService = conversorAClaseLibroService;
//        this.libroService = new LibroService(libroRepo, autorRepo,conversorAClaseLibroService);
//    }
//
//    Scanner leer = new Scanner(System.in);
//
//    public void menu() {
//        var opcion = -1;
//        while (opcion != 0) {
//            var menu = """
//                    1 - Buscar libro por nombre
//                    2 - Buscar libro por lenguaje
//                    3 - Buscar libro por palabra clave
//                    4 - Buscar libro por tema
//                    5 - Mostrar libros mas descargados
//                    6 - Mostrar todos los libros por inicial
//                    7 - Mostrar libros por autor
//                    8 - Listar autores guardados
//                    9 - Listar libros guardados
//                    10 - Listar libros por autores vivos en determinado año
//                    0 - Salir
//                    """;
//            System.out.println(menu);
//            opcion = leer.nextInt();
//            leer.nextLine();
//
//            switch (opcion) {
//                case 1:
//
//                    libroService.listarLibrosPorNombre();
//                    break;
//                case 2:
//
//                    libroService.listarLibroPorIdioma();
//                    break;
//                case 3:
//                    libroService.listarLibroPorPalabraClave();
//                    break;
//                case 4:
//                    libroService.listarLibroPorTema();
//                    break;
//                case 5:
//                    libroService.listar20LibrosMasDescargados();
//                    break;
//                case 6:
//                    libroService.listarLibrosPorInicial();
//                    break;
//                case 7:
//                    libroService.listarLibroPorAutor();
//                    break;
//                case 8:
//                    libroService.listarAutores();
//                    break;
//                case 9 :
//                    libroService.listarLibros();
//                    break;
//                case 10:
//                    libroService.listarLibrosDeAutoresVivosPorAnio();
//                    break;
//                case 0:
//                    System.out.println("Cerrando la aplicación...");
//                    break;
//                default:
//                    System.out.println("Opción inválida");
//            }
//        }
//
//    }
//}
