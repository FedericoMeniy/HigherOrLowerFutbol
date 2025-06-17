package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.*;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Estadistica;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.FutbolistaRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.FutbolApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FutbolApiService {
    private final String API_URL = "https://v3.football.api-sports.io";
    private final String API_KEY = "9909b6a71e75872ff7f27fbb99fb3801";
    private final RestTemplate restTemplate = new RestTemplate();

    //public void importarFutbolistasDesdeApi(int ligaId, int temporada)

    // Utilidad para evitar nulls
    private Integer getSafeInt(JsonNode node, String campoPadre, String campoHijo) {
        JsonNode padre = node.get(campoPadre);
        if (padre != null && padre.has(campoHijo) && !padre.get(campoHijo).isNull()) {
            return padre.get(campoHijo).asInt();
        }
        return 0;
    }
    //Obtener jugador por id
    public String obtenerJugadorPorId(int jugadorId, int temporada) {
        String url = API_URL + "/players?id=" + jugadorId + "&season=" + temporada;

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


   public Optional<FutbolistaDTO> futbolistaApiToDTO(Integer futbolistaId, Integer temporada)throws FutbolApiException {
    Optional<FutbolistaDTO> resultado = Optional.empty();

    if(futbolistaId == null || temporada == null){
        throw new FutbolApiException("playerId o season no pueden ser nulos.");
    } else{

        String url = API_URL + "/players/statistics?id=" + futbolistaId + "&season=" + temporada;
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apisports-key", API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<PlayerStatsApiResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, PlayerStatsApiResponse.class);
            PlayerStatsApiResponse apiResponse = response.getBody();

            if(apiResponse != null && apiResponse.getResponse() != null && !apiResponse.getResponse().isEmpty()){

                PlayerStatsData data = apiResponse.getResponse().get(0);

                if (data != null && data.getPlayer() != null && data.getStatistics() != null && !data.getStatistics().isEmpty()) {
                    PlayerDetails playerDetails = data.getPlayer();
                    Statistic stats = data.getStatistics().get(0);

                    FutbolistaDTO futbolistaJuegoDTO = new FutbolistaDTO();
                    futbolistaJuegoDTO.setIdApi(playerDetails.getId());
                    futbolistaJuegoDTO.setNombre(playerDetails.getName());
                    futbolistaJuegoDTO.setImagen(playerDetails.getPhoto());

                    futbolistaJuegoDTO.setGoles(Optional.ofNullable(stats.getGoals()).map(GoalStats::getTotal).orElse(0));
                    futbolistaJuegoDTO.setAsistencias(Optional.ofNullable(stats.getGoals()).map(GoalStats::getAssists).orElse(0));

                    futbolistaJuegoDTO.setTarjetasAmarillas(Optional.ofNullable(stats.getCards()).map(CardStats::getYellow).orElse(0));
                    futbolistaJuegoDTO.setTarjetasRojas(Optional.ofNullable(stats.getCards()).map(CardStats::getRed).orElse(0));


                    resultado = Optional.of(futbolistaJuegoDTO);
                }
            }
        }catch (Exception e){
            throw new FutbolApiException("Error al pasar el futbolista de la API al DTO");
        }
    }
    return resultado;
   }


    public List<PlayerStatsData> loadPlayersAndStatsFromApiFootball(int leagueId, int season) throws FutbolApiException {
        List<PlayerStatsData> allPlayersData = new ArrayList<>();
        int currentPage = 1;
        int totalPages = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apisports-key", API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        do {
            String url = API_URL + "/players?league=" + leagueId + "&season=" + season + "&page=" + currentPage;
            System.out.println("Consultando API Football: " + url);

            try {

                ResponseEntity<JugadoresApiResponse> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        JugadoresApiResponse.class
                );

                JugadoresApiResponse apiResponse = responseEntity.getBody();


                if (apiResponse != null && apiResponse.getResponse() != null && !apiResponse.getResponse().isEmpty()) {
                    allPlayersData.addAll(apiResponse.getResponse());


                    if (apiResponse.getPaging() != null && apiResponse.getPaging().getTotal() != null) {
                        totalPages = apiResponse.getPaging().getTotal();
                    } else {

                        System.out.println("Advertencia: Información de paginación no disponible. Asumiendo que es la última página.");
                        totalPages = currentPage;
                    }
                } else {
                    System.out.println("Respuesta vacía para la página " + currentPage + ". Terminando paginación.");
                    totalPages = currentPage;
                }

            } catch (Exception e) {
                System.err.println("Error al cargar jugadores de la API para la página " + currentPage + ": " + e.getMessage());
                throw new FutbolApiException("Error al comunicarse con la API Football para cargar jugadores");
            }

            currentPage++;

        } while (currentPage <= totalPages);

        return allPlayersData;
    }


}



