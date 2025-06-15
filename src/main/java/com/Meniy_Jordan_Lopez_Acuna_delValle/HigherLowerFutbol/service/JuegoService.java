package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.FutbolistaDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PreguntaHigherLower;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RondaResultado;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.FutbolistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class JuegoService {

    @Autowired private FutbolistaRepository futbolistaRepository;
    @Autowired private FutbolApiService futbolApiService;
    private final Random random = new Random();

    /*
    public RondaResultado jugarRonda(String eleccionDelUsuario) {
        List<Futbolista> futbolistas = futbolistaRepository.findAll();
        if (futbolistas.size() < 2) {
            throw new IllegalStateException("No hay suficientes futbolistas en la BD para jugar.");
        }
        Collections.shuffle(futbolistas);

        Futbolista f1 = asegurarEstadisticas(futbolistas.get(0));
        Futbolista f2 = asegurarEstadisticas(futbolistas.get(1));

        PreguntaHigherLower pregunta = PreguntaHigherLower.obtenerPregunta();
        int valorF1 = f1.getGoles();
        int valorF2 = f2.getGoles();

        boolean esCorrecto;
        if ("mayor".equalsIgnoreCase(eleccionDelUsuario)) {
            esCorrecto = valorF2 > valorF1;
        } else if ("menor".equalsIgnoreCase(eleccionDelUsuario)) {
            esCorrecto = valorF2 < valorF1;
        } else {
            throw new IllegalArgumentException("La elección debe ser 'mayor' o 'menor'.");
        }

        RondaResultado resultado = new RondaResultado();
        resultado.setFutbolista1(f1);
        resultado.setFutbolista2(f2);
        resultado.setPregunta(pregunta);
        resultado.setValorFutbolista1(valorF1);
        resultado.setValorFutbolista2(valorF2);
        resultado.setBooleano(esCorrecto);
        return resultado;
    }

    private Futbolista asegurarEstadisticas(Futbolista futbolista) {
        if (futbolista.getGoles() == 0 && futbolista.getPartidosJugados() == 0) {
            System.out.println("Stats no encontradas para " + futbolista.getNombre() + ". Buscando en la API...");
            try {
                Optional<FutbolistaDTO> dtoOpt = futbolApiService.futbolistaApiToDTO(futbolista.getIdApi(), 2023);
                if (dtoOpt.isPresent()) {
                    FutbolistaDTO dto = dtoOpt.get();
                    futbolista.setGoles(dto.getGoles());
                    futbolista.setAsistencias(dto.getAsistencias());
                    futbolista.setTarjetasAmarillas(dto.getTarjetasAmarillas());
                    futbolista.setTarjetasRojas(dto.getTarjetasRojas());
                    return futbolistaRepository.save(futbolista);
                }
            } catch (FutbolApiException e) {
                System.err.println("No se pudieron obtener las stats para el jugador " + futbolista.getIdApi());
            }
        }
        return futbolista;
    }
    */

}
    /*
 public RondaResultado jugarRonda(String eleccionMayorOMenor) {
        // 1. Elegir dos futbolistas
        Futbolista[] futbolistas = elegirDosFutbolistasAleatorios();
        Futbolista f1 = futbolistas[0];
        Futbolista f2 = futbolistas[1];

        // 2. Elegir pregunta al azar
        PreguntaHigherLower pregunta = PreguntaHigherLower.obtenerPregunta();

        // 3. Obtener valores para cada futbolista
        int valorF1 = f1.getStatistic().obtenerValorPorPregunta(pregunta);
        int valorF2 = f2.getStatistic().obtenerValorPorPregunta(pregunta);

        // 4. Evaluar respuesta del jugador
        boolean acerto;
        if ("mayor".equalsIgnoreCase(eleccionMayorOMenor)) {
            acerto = valorF2 > valorF1;
        } else if ("menor".equalsIgnoreCase(eleccionMayorOMenor)) {
            acerto = valorF2 < valorF1;
        } else {
            throw new IllegalArgumentException("Elección inválida, debe ser 'mayor' o 'menor'");
        }

        // 5. Armar resultado para devolver
        RondaResultado resultado = new RondaResultado();
        resultado.setFutbolista1(f1);
        resultado.setFutbolista2(f2);
        resultado.setPregunta(pregunta);
        resultado.setValorFutbolista1(valorF1);
        resultado.setValorFutbolista2(valorF2);
        resultado.setAcerto(acerto);

        return resultado;
    }


     */





