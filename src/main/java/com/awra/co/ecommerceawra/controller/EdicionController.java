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

import com.awra.co.ecommerceawra.dto.EdicionRequest;
import com.awra.co.ecommerceawra.dto.EdicionResponse;
import com.awra.co.ecommerceawra.service.EdicionService;

import jakarta.validation.Valid;

@Tag(name = "Admin - Ediciones de la Home")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/sitio/ediciones")
public class EdicionController {

    private final EdicionService edicionService;

    public EdicionController(EdicionService edicionService) {
        this.edicionService = edicionService;
    }

    @GetMapping
    public List<EdicionResponse> listar() {
        return edicionService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EdicionResponse crear(@Valid @RequestBody EdicionRequest request) {
        return edicionService.crear(request);
    }

    @PutMapping("/{id}")
    public EdicionResponse actualizar(@PathVariable Long id, @Valid @RequestBody EdicionRequest request) {
        return edicionService.actualizar(id, request);
    }
}
