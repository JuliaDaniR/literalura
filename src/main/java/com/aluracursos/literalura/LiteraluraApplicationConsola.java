//package com.aluracursos.literalura;
//
//import com.aluracursos.literalura.Principal.Principal;
//import com.aluracursos.literalura.repository.IAutorRepository;
//import com.aluracursos.literalura.repository.ILibroRepository;
//import com.aluracursos.literalura.service.ConversorAClaseLibroService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class LiteraluraApplicationConsola implements CommandLineRunner {
//    @Autowired
//    private ILibroRepository libroRepo;
//    @Autowired
//    private IAutorRepository autorRepo;
//    @Autowired
//    private ConversorAClaseLibroService conversorAClaseLibroService;
//
//    public static void main(String[] args) {
//        SpringApplication.run(LiteraluraApplicationConsola.class, args);
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        Principal principal = new Principal(libroRepo, autorRepo,conversorAClaseLibroService );
//        principal.menu();
//    }
//}
