package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.ProductoPublicoDetalleResponse;
import com.awra.co.ecommerceawra.dto.ProductoPublicoResumenResponse;
import com.awra.co.ecommerceawra.service.ProductoPublicoService;

@Tag(name = "Público - Catálogo")
@RestController
@RequestMapping("/api/publico/productos")
public class PublicoProductoController {

    private final ProductoPublicoService productoPublicoService;

    public PublicoProductoController(ProductoPublicoService productoPublicoService) {
        this.productoPublicoService = productoPublicoService;
    }

    @GetMapping
    public PagedModel<ProductoPublicoResumenResponse> listar(
            @RequestParam(required = false) Long categoriaId,
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return productoPublicoService.listar(categoriaId, pageable);
    }

    @GetMapping("/{id}")
    public ProductoPublicoDetalleResponse obtener(@PathVariable Long id) {
        return productoPublicoService.obtener(id);
    }
}
