package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.EquipoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.FutbolistaDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Equipo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.EquipoRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.FutbolistaRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FutbolistaDataSyncService {

    @Autowired private FutbolApiService futbolApiService;
    @Autowired private FutbolistaRepository futbolistaRepository;
    @Autowired private EquipoRepository equipoRepository;

    public void sincronizarLigaEntera(int ligaId, int temporada) {
        System.out.println("== INICIANDO SINCRONIZACIÓN COMPLETA PARA LIGA " + ligaId + " ==");
        List<EquipoDTO.EquipoInfo> equiposDesdeApi = futbolApiService.obtenerEquiposPorLiga(ligaId, temporada);

        for (EquipoDTO.EquipoInfo equipoInfo : equiposDesdeApi) {
            Equipo equipo = equipoRepository.findByIdApi(equipoInfo.getId()).orElse(new Equipo());
            equipo.setIdApi(equipoInfo.getId());
            equipo.setNombre(equipoInfo.getName());
            equipoRepository.save(equipo);

            try {
                sincronizarJugadoresDeEquipo(equipo.getIdApi(), temporada);
            } catch (Exception e) {
                System.err.println("Error al sincronizar jugadores del equipo " + equipo.getNombre() + ": " + e.getMessage());
            }
        }
        System.out.println("== SINCRONIZACIÓN COMPLETA DE LIGA FINALIZADA ==");
    }

    public void sincronizarJugadoresDeEquipo(int equipoId, int temporada) throws Exception {
        String jugadoresJsonString = futbolApiService.obtenerJugadorPorLiga(equipoId);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jugadoresJsonString);

        // --- INICIO DE LA MODIFICACIÓN ---
        JsonNode responseArray = rootNode.path("response");

        // Verificación de seguridad: nos aseguramos de que la respuesta no esté vacía
        if (responseArray.isEmpty() || !responseArray.has(0)) {
            System.out.println("ADVERTENCIA: No se recibieron jugadores para el equipo ID: " + equipoId + ". Posiblemente no hay datos en la API o se alcanzó el límite de peticiones. Saltando equipo.");
            return; // Salimos del método para este equipo y continuamos con el siguiente.
        }

        // Si la verificación pasa, continuamos como antes
        JsonNode responseNode = responseArray.get(0).path("players");
        // --- FIN DE LA MODIFICACIÓN ---


        List<FutbolistaDTO> jugadoresSquad = objectMapper.convertValue(responseNode, new TypeReference<>() {});

        for (FutbolistaDTO jugadorInfo : jugadoresSquad) {
            if (jugadorInfo.getIdApi() == null) continue;

            if (!futbolistaRepository.findByIdApi(jugadorInfo.getIdApi()).isPresent()) {
                Futbolista nuevoFutbolista = new Futbolista();
                nuevoFutbolista.setIdApi(jugadorInfo.getIdApi());
                nuevoFutbolista.setNombre(jugadorInfo.getNombre());
                nuevoFutbolista.setImagenURL(jugadorInfo.getImagen());
                nuevoFutbolista.setGoles(0);
                nuevoFutbolista.setAsistencias(0);
                nuevoFutbolista.setTarjetasAmarillas(0);
                nuevoFutbolista.setTarjetasRojas(0);
                nuevoFutbolista.setPartidosJugados(0);
                futbolistaRepository.save(nuevoFutbolista);
                System.out.println("Futbolista BÁSICO '" + nuevoFutbolista.getNombre() + "' guardado.");
            }
        }
        // No es necesario imprimir el "ya existe" para no llenar la consola
    }
}


    /// creo que llama los jugadores de un equipo en particular con sus datos
    /// ADVERTENCIA: puede que use muchas request porque utiliza UN llamado a la api por jugador, que es donde estan los datos que buscamos
    /*
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

    */

