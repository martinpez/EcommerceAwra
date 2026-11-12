package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.MovimientoRequest;
import com.awra.co.ecommerceawra.dto.MovimientoResponse;
import com.awra.co.ecommerceawra.service.MovimientoService;

import jakarta.validation.Valid;

@Tag(name = "Admin - Movimientos de inventario")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/variantes/{varianteId}/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoResponse registrar(@PathVariable Long varianteId, @Valid @RequestBody MovimientoRequest request) {
        return movimientoService.registrar(varianteId, request);
    }

    @GetMapping
    public List<MovimientoResponse> historial(@PathVariable Long varianteId) {
        return movimientoService.historial(varianteId);
    }
}
