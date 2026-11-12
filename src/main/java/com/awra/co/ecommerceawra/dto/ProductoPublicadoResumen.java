package com.awra.co.ecommerceawra.dto;

import java.math.BigDecimal;

public record ProductoPublicadoResumen(
        Long productoId,
        String nombre,
        BigDecimal precio,
        Long categoriaId,
        String categoriaNombre,
        Long stockTotal) {
}
