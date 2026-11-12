package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.EmpresaActualizarRequest;
import com.awra.co.ecommerceawra.dto.EmpresaResponse;
import com.awra.co.ecommerceawra.service.EmpresaService;

import jakarta.validation.Valid;

@Tag(name = "Admin - Empresa")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/sitio/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public EmpresaResponse obtener() {
        return empresaService.obtener();
    }

    @PutMapping
    public EmpresaResponse actualizar(@Valid @RequestBody EmpresaActualizarRequest request) {
        return empresaService.actualizar(request);
    }
}
