package com.awra.co.ecommerceawra.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.awra.co.ecommerceawra.entity.EstadoPublicacion;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Long categoriaId,
        String categoriaNombre,
        EstadoPublicacion estadoPublicacion,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        List<VarianteResponse> variantes) {
}
