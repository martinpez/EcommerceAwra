package com.awra.co.ecommerceawra.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoPublicoDetalleResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Long categoriaId,
        String categoriaNombre,
        DisponibilidadProducto disponibilidad,
        String etiqueta,
        List<VariantePublicaResponse> variantes,
        List<ImagenPublicaResponse> imagenes) {
}
