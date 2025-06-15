package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.FutbolistaDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.FutbolistaRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FutbolistaDataSyncService {

    @Autowired
    private FutbolApiService futbolApiService;

    @Autowired
    private FutbolistaRepository futbolistaRepository;

    /// creo que llama los jugadores de un equipo en particular con sus datos
    /// ADVERTENCIA: puede que use muchas request porque utiliza UN llamado a la api por jugador, que es donde estan los datos que buscamos

    public void sincronizarJugadoresDeEquipo(int equipoId, int temporada) throws Exception {
        System.out.println("Iniciando sincronización para el equipo: " + equipoId);

        // 1. Obtener la lista de jugadores del equipo desde la API
        String jugadoresJsonString = futbolApiService.obtenerJugadorPorLiga(equipoId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jugadoresJsonString);
        JsonNode responseNode = rootNode.path("response").get(0).path("players");

        List<FutbolistaDTO> jugadoresSquad = objectMapper.convertValue(responseNode, new TypeReference<>() {});

        // 2. Por cada jugador, obtener sus stats y guardarlo si no existe
        for (FutbolistaDTO jugadorInfo : jugadoresSquad) {
            Integer idApi = jugadorInfo.getIdApi();
            if (idApi == null) continue;

            // Verificamos si ya tenemos este futbolista en nuestra BD
            Optional<Futbolista> existente = futbolistaRepository.findByIdApi(idApi);

            if (existente.isEmpty()) {
                // Si no existe, obtenemos sus estadísticas completas
                System.out.println("Futbolista nuevo encontrado (ID API: " + idApi + "). Obteniendo stats...");
                try {
                    Optional<FutbolistaDTO> futbolistaConStatsOpt = futbolApiService.futbolistaApiToDTO(idApi, temporada);

                    if (futbolistaConStatsOpt.isPresent()) {
                        FutbolistaDTO futbolistaCompleto = futbolistaConStatsOpt.get();

                        // Creamos la entidad y la guardamos
                        Futbolista nuevoFutbolista = new Futbolista();
                        nuevoFutbolista.setIdApi(futbolistaCompleto.getIdApi());
                        nuevoFutbolista.setNombre(futbolistaCompleto.getNombre());
                        nuevoFutbolista.setImagenURL(futbolistaCompleto.getImagen());
                        nuevoFutbolista.setGoles(futbolistaCompleto.getGoles());
                        nuevoFutbolista.setAsistencias(futbolistaCompleto.getAsistencias());
                        nuevoFutbolista.setTarjetasAmarillas(futbolistaCompleto.getTarjetasAmarillas());
                        nuevoFutbolista.setTarjetasRojas(futbolistaCompleto.getTarjetasRojas());

                        futbolistaRepository.save(nuevoFutbolista);
                        System.out.println("Futbolista '" + nuevoFutbolista.getNombre() + "' guardado en la base de datos.");
                    }
                } catch (FutbolApiException e) {
                    System.err.println("Error al obtener stats para el jugador con ID API " + idApi + ": " + e.getMessage());
                }
            } else {
                System.out.println("Futbolista '" + existente.get().getNombre() + "' (ID API: " + idApi + ") ya existe. Saltando...");
            }
        }
        System.out.println("Sincronización para el equipo " + equipoId + " completada.");
    }
}
