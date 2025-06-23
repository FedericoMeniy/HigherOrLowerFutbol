package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.AuthenticationResponse;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.LoginDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PerfilDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PuntajeDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RegistroDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.JugadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse registrarJugador(RegistroDTO request) {
        if (jugadorRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("El nombre de usuario ya existe.");
        }

        if (jugadorRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("El email ya se encuentra registrado.");
        }

        var jugador = new Jugador();
        jugador.setUsername(request.getUsername());
        jugador.setEmail(request.getEmail());
        jugador.setPassword(passwordEncoder.encode(request.getPassword()));


        jugador.setTipoRol("USER");

        jugadorRepository.save(jugador);


        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("userId", jugador.getId());

        var jwtToken = jwtService.generateToken(extraClaims, jugador);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse autenticarJugador(LoginDTO request) {
        Jugador jugador = jugadorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("El email no se encuentra registrado.")); //

        if (!passwordEncoder.matches(request.getPassword(), jugador.getPassword())) {

            throw new BadCredentialsException("La contraseña es incorrecta.");
        }


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("userId", jugador.getId());

        var jwtToken = jwtService.generateToken(extraClaims, jugador);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .tipoRol(jugador.getTipoRol())
                .build();
    }

    @Transactional
    public int guardarPuntaje(String email, PuntajeDTO puntajeDTO) {
        Jugador jugador = jugadorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        int nuevosPuntos = jugador.getPuntaje() + puntajeDTO.getPuntos();
        jugador.setPuntaje(nuevosPuntos);
        jugadorRepository.save(jugador);
        return nuevosPuntos;
    }

    public PerfilDTO obtenerPerfil(String email) {
        Jugador jugador = jugadorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        return PerfilDTO.builder()
                .username(jugador.getNombreUsuario())
                .email(jugador.getEmail())
                .puntosTotales(jugador.getPuntaje())
                .build();
    }
}