package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.config;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("==> Iniciando filtro JWT para la petición: {}", request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("==> Cabecera de autorización ausente o incorrecta. Pasando al siguiente filtro.");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);
            logger.info("==> Email extraído del token: {}", userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.info("==> Contexto de seguridad vacío. Intentando cargar usuario '{}'.", userEmail);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                logger.info("==> Usuario '{}' cargado. Roles: {}", userDetails.getUsername(), userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    logger.info("==> El token es VÁLIDO. Creando token de autenticación.");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("==> Usuario '{}' AUTENTICADO y contexto actualizado.", userEmail);
                } else {
                    logger.warn("==> El token para el usuario '{}' NO es válido.", userEmail);
                }
            }
        } catch (Exception e) {

            logger.error("==> EXCEPCIÓN durante el procesamiento del filtro JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}