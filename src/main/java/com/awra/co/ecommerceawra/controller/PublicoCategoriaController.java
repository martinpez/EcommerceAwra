package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.CategoriaPublicaResponse;
import com.awra.co.ecommerceawra.service.ProductoPublicoService;

@Tag(name = "Público - Categorías")
@RestController
@RequestMapping("/api/publico/categorias")
public class PublicoCategoriaController {

    private final ProductoPublicoService productoPublicoService;

    public PublicoCategoriaController(ProductoPublicoService productoPublicoService) {
        this.productoPublicoService = productoPublicoService;
    }

    @GetMapping
    public List<CategoriaPublicaResponse> listar() {
        return productoPublicoService.listarCategorias();
    }
}
