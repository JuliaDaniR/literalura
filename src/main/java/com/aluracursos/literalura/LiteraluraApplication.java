package com.aluracursos.literalura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class LiteraluraApplication{

    public static void main(String[] args) {
        SpringApplication.run(LiteraluraApplication.class, args);
    }
}
