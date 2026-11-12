package com.awra.co.ecommerceawra.dto;

import java.time.LocalDateTime;

import com.awra.co.ecommerceawra.entity.TipoAlerta;

public record AlertaStockResponse(
        Long id,
        Long varianteId,
        String productoNombre,
        String talla,
        String color,
        String sku,
        TipoAlerta tipo,
        int stockAlMomento,
        boolean leida,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaResolucion) {
}
