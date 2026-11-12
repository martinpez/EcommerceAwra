package com.awra.co.ecommerceawra.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ErrorAccesoDenegadoHandler implements AccessDeniedHandler {

    private final RespuestaSeguridad respuestaSeguridad;

    public ErrorAccesoDenegadoHandler(RespuestaSeguridad respuestaSeguridad) {
        this.respuestaSeguridad = respuestaSeguridad;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        respuestaSeguridad.escribir(request, response, HttpStatus.FORBIDDEN,
                "No tienes permiso para esta operación");
    }
}
