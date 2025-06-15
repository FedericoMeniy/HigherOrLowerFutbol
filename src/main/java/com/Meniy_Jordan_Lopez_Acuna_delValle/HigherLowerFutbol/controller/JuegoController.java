package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;



import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RespuestaJuego;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Estadistica;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/juego")
@CrossOrigin(origins = "*")
public class JuegoController {

    // --- Base de Datos de Futbolistas (Ejemplo) ---
    private static final List<Futbolista> FUTBOLISTAS_DB = List.of( // <-- Cambiado
             //CARGAR FUTBOLISTAS ACA
    );
    private static final List<String> ESTADISTICAS_POSIBLES = List.of("goles", "asistencias");
    private final Random random = new Random();
    // ---------------------------------------------


    @GetMapping("/iniciar")
    public RespuestaJuego iniciarJuego() {
        // 1. Elegir dos futbolistas diferentes al azar
        int index1 = random.nextInt(FUTBOLISTAS_DB.size());
        int index2;
        do {
            index2 = random.nextInt(FUTBOLISTAS_DB.size());
        } while (index1 == index2);

        Futbolista futbolista1 = FUTBOLISTAS_DB.get(index1); // <-- Cambiado
        Futbolista futbolista2 = FUTBOLISTAS_DB.get(index2); // <-- Cambiado

        // 2. Elegir una estadística al azar para comparar
        String estadistica = ESTADISTICAS_POSIBLES.get(random.nextInt(ESTADISTICAS_POSIBLES.size()));

        // 3. Crear el objeto de respuesta y poblarlo con los datos
        RespuestaJuego respuesta = new RespuestaJuego();
        respuesta.setFutbolistaActual(futbolista1);   // <-- Cambiado
        respuesta.setFutbolistaSiguiente(futbolista2); // <-- Cambiado
        respuesta.setEstadisticaActual(estadistica);

        return respuesta;
    }
}