package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.VarianteRequest;
import com.awra.co.ecommerceawra.dto.VarianteResponse;
import com.awra.co.ecommerceawra.service.VarianteService;

import jakarta.validation.Valid;

@Tag(name = "Admin - Variantes")
@SecurityRequirement(name = "bearer-key")
@RestController
public class VarianteController {

    private final VarianteService varianteService;

    public VarianteController(VarianteService varianteService) {
        this.varianteService = varianteService;
    }

    @PostMapping("/api/admin/productos/{productoId}/variantes")
    @ResponseStatus(HttpStatus.CREATED)
    public VarianteResponse crear(@PathVariable Long productoId, @Valid @RequestBody VarianteRequest request) {
        return varianteService.crear(productoId, request);
    }

    @PutMapping("/api/admin/variantes/{id}")
    public VarianteResponse actualizar(@PathVariable Long id, @Valid @RequestBody VarianteRequest request) {
        return varianteService.actualizar(id, request);
    }

    @DeleteMapping("/api/admin/variantes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        varianteService.eliminar(id);
    }
}
