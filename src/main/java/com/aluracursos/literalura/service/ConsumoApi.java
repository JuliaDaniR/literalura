package com.aluracursos.literalura.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ConsumoApi {

    private final RestClient restClient;

    public ConsumoApi() {
        this.restClient = RestClient.create();
    }

    public String obtenerDatos(String url) {
        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Error al consumir la API en la URL: " + url, e);
        }
    }
}
