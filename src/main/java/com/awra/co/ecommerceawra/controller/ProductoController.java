package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.ProductoActualizarRequest;
import com.awra.co.ecommerceawra.dto.ProductoAdminResumenResponse;
import com.awra.co.ecommerceawra.dto.PublicacionRequest;
import com.awra.co.ecommerceawra.dto.ProductoCrearRequest;
import com.awra.co.ecommerceawra.dto.ProductoResponse;
import com.awra.co.ecommerceawra.entity.EstadoPublicacion;
import com.awra.co.ecommerceawra.service.ProductoService;

import jakarta.validation.Valid;

@Tag(name = "Admin - Productos")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public PagedModel<ProductoAdminResumenResponse> listar(
            @RequestParam(required = false) EstadoPublicacion estado,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return productoService.listarParaAdmin(estado, categoriaId, q, pageable);
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoCrearRequest request) {
        return productoService.crear(request);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoActualizarRequest request) {
        return productoService.actualizar(id, request);
    }

    @PatchMapping("/{id}/publicacion")
    public ProductoResponse cambiarPublicacion(@PathVariable Long id, @Valid @RequestBody PublicacionRequest request) {
        return productoService.cambiarPublicacion(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
