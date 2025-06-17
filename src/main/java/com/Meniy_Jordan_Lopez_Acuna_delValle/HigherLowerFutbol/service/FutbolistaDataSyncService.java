package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.FutbolistaDTO;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PlayerStatsData;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.Statistic;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Estadistica;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.FutbolistaRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.GoalStats;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.CardStats;

@Service
@Data
public class FutbolistaDataSyncService {

    @Autowired private FutbolApiService futbolApiService;
    @Autowired private FutbolistaRepository futbolistaRepository;


    @Transactional
    public int initialLoadPlayersAndStats(int leagueId, int season) throws FutbolApiException {
        System.out.println("\n--- Iniciando carga/sincronización de futbolistas (solo goles y tarjetas) para Liga " + leagueId + " temporada " + season + " ---");

        List<PlayerStatsData> playersFromApi = futbolApiService.loadPlayersAndStatsFromApiFootball(leagueId, season);
        int processedCount = 0;

        for (PlayerStatsData playerStatsData : playersFromApi) {
            if (playerStatsData != null && playerStatsData.getPlayer() != null && playerStatsData.getPlayer().getId() != null) {
                Integer idApi = playerStatsData.getPlayer().getId();

                Optional<Futbolista> existingFutbolistaOpt = futbolistaRepository.findByIdApi(idApi);
                Futbolista futbolista;
                Estadistica estadistica;

                if (existingFutbolistaOpt.isPresent()) {
                    futbolista = existingFutbolistaOpt.get();
                    System.out.println("  > Actualizando futbolista existente: " + playerStatsData.getPlayer().getName() + " (ID API: " + idApi + ")");

                    estadistica = futbolista.getEstadistica();
                    if (estadistica == null) {
                        estadistica = new Estadistica();
                        estadistica.setFutbolista(futbolista);
                        futbolista.setEstadistica(estadistica);
                    }
                } else {
                    futbolista = new Futbolista();
                    futbolista.setIdApi(idApi);
                    System.out.println("  > Insertando nuevo futbolista: " + playerStatsData.getPlayer().getName() + " (ID API: " + idApi + ")");

                    estadistica = new Estadistica();
                    estadistica.setFutbolista(futbolista);
                    futbolista.setEstadistica(estadistica);
                }


                futbolista.setNombre(playerStatsData.getPlayer().getName());
                futbolista.setImagenURL(playerStatsData.getPlayer().getPhoto());


                if (playerStatsData.getStatistics() != null && !playerStatsData.getStatistics().isEmpty()) {
                    Statistic apiStats = playerStatsData.getStatistics().get(0);

                    estadistica.setGoles(Optional.ofNullable(apiStats.getGoals()).map(GoalStats::getTotal).orElse(0));
                    estadistica.setAsistencias(Optional.ofNullable(apiStats.getGoals()).map(GoalStats::getAssists).orElse(0));

                    estadistica.setTarjetasAmarillas(Optional.ofNullable(apiStats.getCards()).map(CardStats::getYellow).orElse(0));
                    estadistica.setTarjetasRojas(Optional.ofNullable(apiStats.getCards()).map(CardStats::getRed).orElse(0));


                } else {

                    estadistica.setGoles(0);
                    estadistica.setAsistencias(0);
                    estadistica.setTarjetasAmarillas(0);
                    estadistica.setTarjetasRojas(0);
                    System.out.println("Advertencia: No se encontraron estadísticas detalladas para el jugador " + futbolista.getNombre() + " (ID: " + idApi + "). Se inicializan en 0.");
                }


                try {
                    futbolistaRepository.save(futbolista);
                    processedCount++;
                } catch (Exception e) {
                    System.err.println("Error al guardar/actualizar el futbolista " + futbolista.getNombre() + " (ID API: " + idApi + "): " + e.getMessage());
                    e.printStackTrace();
                }

            } else {
                System.out.println("Advertencia: Se encontró un objeto de jugador nulo o incompleto en la respuesta de la API. Se omite.");
            }
        }
        System.out.println("--- Carga/sincronización finalizada. Total de futbolistas procesados: " + processedCount + " ---");
        return processedCount;
    }

 }

