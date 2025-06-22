
package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Torneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.enums.EstadoTorneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    boolean existsByNombre(String nombre);
    List<Torneo> findByEstadoTorneoAndFechaCreacionBefore(EstadoTorneo estadoTorneo, LocalDateTime fecha);
    List<Torneo> findByCreadorId(Long creadorId);

}