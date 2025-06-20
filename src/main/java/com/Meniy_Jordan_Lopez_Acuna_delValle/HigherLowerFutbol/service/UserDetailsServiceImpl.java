package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Spring Security llamará a este método pasándole el email que el usuario usó para loguearse.
        // Nosotros lo buscamos en la base de datos y lo devolvemos.
        return jugadorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));
    }
}