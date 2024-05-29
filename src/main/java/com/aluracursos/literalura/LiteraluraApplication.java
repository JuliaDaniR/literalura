package com.aluracursos.literalura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class LiteraluraApplication{

    public static void main(String[] args) {
        SpringApplication.run(LiteraluraApplication.class, args);
    }
}
