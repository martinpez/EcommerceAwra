package com.awra.co.ecommerceawra.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.awra.co.ecommerceawra.dto.LoginRequest;
import com.awra.co.ecommerceawra.dto.LoginResponse;
import com.awra.co.ecommerceawra.security.JwtProperties;
import com.awra.co.ecommerceawra.security.JwtService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication autenticacion = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));

        List<String> roles = autenticacion.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(autoridad -> autoridad.replaceFirst("^ROLE_", ""))
                // Spring Security 7 agrega internamente "FACTOR_PASSWORD" para
                // marcar que la contraseña se verifico. No es un rol de la
                // aplicacion, asi que no viaja en el token.
                .filter(rol -> !rol.startsWith("FACTOR_"))
                .toList();

        String token = jwtService.generar(autenticacion.getName(), roles);
        return new LoginResponse(token, "Bearer", jwtProperties.expiracionMinutos() * 60);
    }
}
