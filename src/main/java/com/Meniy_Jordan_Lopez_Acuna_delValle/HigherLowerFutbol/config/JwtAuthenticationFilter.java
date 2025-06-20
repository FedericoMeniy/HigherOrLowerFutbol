// Archivo: src/main/java/com/Meniy_Jordan_Lopez_Acuna_delValle/HigherLowerFutbol/config/JwtAuthenticationFilter.java

package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.config;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor // Crea un constructor con los campos final
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Obtener la cabecera "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Comprobar si la cabecera es nula o no empieza con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si no hay token, se pasa al siguiente filtro de la cadena y se termina.
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitando el "Bearer ")
        jwt = authHeader.substring(7);
        // 4. Extraer el email del usuario desde el token
        userEmail = jwtService.extractUsername(jwt);

        // 5. Validar el token
        // Si el email existe y no hay una autenticación ya configurada en el contexto de seguridad...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // ...cargamos los detalles del usuario desde la base de datos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Si el token es válido...
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // ...creamos una autenticación y la establecemos en el contexto de seguridad.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 6. Dejar pasar la petición al siguiente filtro
        filterChain.doFilter(request, response);
    }
}
