package com.awra.co.ecommerceawra.dto;

public record InventarioProductoResumen(
        Long productoId,
        String nombre,
        Long categoriaId,
        String categoriaNombre,
        Long stockTotal) {
}
