package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.InventarioProductoResponse;
import com.awra.co.ecommerceawra.service.InventarioService;

@Tag(name = "Admin - Inventario")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public PagedModel<InventarioProductoResponse> listar(@PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return inventarioService.listar(pageable);
    }
}
