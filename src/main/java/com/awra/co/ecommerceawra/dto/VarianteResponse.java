package com.awra.co.ecommerceawra.dto;

public record VarianteResponse(
        Long id,
        Long productoId,
        String talla,
        String color,
        String sku,
        int stock,
        int stockMinimo) {
}
