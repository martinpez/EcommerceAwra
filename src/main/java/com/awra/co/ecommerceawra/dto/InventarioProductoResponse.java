package com.awra.co.ecommerceawra.dto;

public record InventarioProductoResponse(
        Long productoId,
        String nombre,
        Long categoriaId,
        String categoriaNombre,
        long stockTotal,
        DisponibilidadProducto disponibilidad) {
}
