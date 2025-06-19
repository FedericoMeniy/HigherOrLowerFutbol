package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.DetalleTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleTorneoRepository extends JpaRepository<DetalleTorneo,Long> {

    Optional<DetalleTorneo> findByTorneoAndJugador(Torneo torneo, Jugador jugador);

    List<DetalleTorneo> findByTorneoIdOrderByPuntajeDescPartidasJugadasAsc(Long torneoId);

}
