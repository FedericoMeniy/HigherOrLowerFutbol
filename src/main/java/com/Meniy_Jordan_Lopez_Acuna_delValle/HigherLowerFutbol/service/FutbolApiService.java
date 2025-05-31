package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FutbolApiService {
    private final String API_URL = "https://v3.football.api-sports.io";
    private final String API_KEY = "9909b6a71e75872ff7f27fbb99fb3801";
    private final RestTemplate restTemplate = new RestTemplate();

    public String obtenerEquiposPorLiga(int ligaId, int temporada) {
        String url = API_URL + "/teams?league=" + ligaId + "&season=" + temporada;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apisports-key", API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }

    public String obtenerEstadisticaJugador(int jugadorId, int temporadaId){
        String url = API_URL + "players?id=" + jugadorId + "&season=" + temporadaId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apisports-key", API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }

    public String obtenerJugadorPorLiga(int equipoId){
        String url = API_URL + "/players/squads?team=" + equipoId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apisports-key", API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }

}
