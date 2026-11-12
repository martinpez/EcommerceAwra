package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.SeccionSitioRequest;
import com.awra.co.ecommerceawra.dto.SeccionSitioResponse;
import com.awra.co.ecommerceawra.service.SeccionSitioService;

import jakarta.validation.Valid;

@Tag(name = "Admin - Secciones del sitio")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/sitio/secciones")
public class SeccionSitioController {

    private final SeccionSitioService seccionSitioService;

    public SeccionSitioController(SeccionSitioService seccionSitioService) {
        this.seccionSitioService = seccionSitioService;
    }

    @GetMapping
    public List<SeccionSitioResponse> listar() {
        return seccionSitioService.listar();
    }

    @GetMapping("/{id}")
    public SeccionSitioResponse obtener(@PathVariable Long id) {
        return seccionSitioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeccionSitioResponse crear(@Valid @RequestBody SeccionSitioRequest request) {
        return seccionSitioService.crear(request);
    }

    @PutMapping("/{id}")
    public SeccionSitioResponse actualizar(@PathVariable Long id, @Valid @RequestBody SeccionSitioRequest request) {
        return seccionSitioService.actualizar(id, request);
    }
}
