package com.awra.co.ecommerceawra.dto;

import java.math.BigDecimal;

public record ProductoPublicoResumenResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Long categoriaId,
        String categoriaNombre,
        DisponibilidadProducto disponibilidad,
        String etiqueta,
        /** Foto principal del producto. Es null si todavia no tiene imagenes. */
        ImagenPublicaResponse imagenPrincipal) {
}
