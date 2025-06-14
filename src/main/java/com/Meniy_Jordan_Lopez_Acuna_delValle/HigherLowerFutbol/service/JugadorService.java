package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RegistroDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RespuestaLoginDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JugadorService {
    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registrarJugador(RegistroDTO registroDTO) {
        // Encriptar la contraseña
        String encodedPassword = passwordEncoder.encode(registroDTO.getPassword());

        // Crear un nuevo jugador
        Jugador jugador = new Jugador();
        jugador.setUsername(registroDTO.getUsername());
        jugador.setEmail(registroDTO.getEmail());
        jugador.setPassword(encodedPassword);
        jugador.setTipoRol(registroDTO.getTipoRol());

        // Guardar en la base de datos
        jugadorRepository.save(jugador);
    }

    public RespuestaLoginDTO autenticarJugador(String email, String password) {
        Optional<Jugador> jugadorOptional = jugadorRepository.findByEmail(email);

        if(jugadorOptional.isEmpty()){
            return new RespuestaLoginDTO(false, "El email ingresado no esta registrado.");
        }

        Jugador jugador = jugadorOptional.get();

        if(!passwordEncoder.matches(password, jugador.getPassword())){
            return new RespuestaLoginDTO(false, "La contraseña es incorrecta.");
        }

        return new RespuestaLoginDTO(true, "Login exitoso.");
    }
}

