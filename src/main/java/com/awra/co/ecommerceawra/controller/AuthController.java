package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.AuthMeResponse;
import com.awra.co.ecommerceawra.dto.LoginRequest;
import com.awra.co.ecommerceawra.dto.LoginResponse;
import com.awra.co.ecommerceawra.service.AuthService;

import jakarta.validation.Valid;

@Tag(name = "Autenticación")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Datos de la sesion actual.
     *
     * El panel lo llama al abrirse: si el token ya no es valido, la API
     * responde 401 y el panel vuelve a la pantalla de acceso.
     */
    @GetMapping("/me")
    public AuthMeResponse me(@AuthenticationPrincipal Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        return new AuthMeResponse(jwt.getSubject(), roles == null ? List.of() : roles);
    }
}
