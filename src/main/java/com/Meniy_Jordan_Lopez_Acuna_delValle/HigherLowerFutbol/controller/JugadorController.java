package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.LoginDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RegistroDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RespuestaLoginDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JugadorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")

public class JugadorController {
    @Autowired
    private JugadorService jugadorService;

    // MÉTODO ACTUALIZADO
    @PostMapping("/register")
    public ResponseEntity<String> registrar(@RequestBody RegistroDTO registroDTO){
        try {
            jugadorService.registrarJugador(registroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Jugador registrado exitosamente.");
        } catch (IllegalStateException e) {
            // Si el servicio lanza nuestra excepción, la capturamos
            // y devolvemos un error 400 (Bad Request) con el mensaje.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<RespuestaLoginDTO> login(@RequestBody LoginDTO loginDTO){
        RespuestaLoginDTO respuesta = jugadorService.autenticarJugador(loginDTO.getEmail(), loginDTO.getPassword());

        if(respuesta.isExito()){
            return ResponseEntity.ok(respuesta);
        }else{
            if(respuesta.getMensaje().contains("email")){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
            }
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

}

