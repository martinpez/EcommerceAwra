package com.awra.co.ecommerceawra.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.awra.co.ecommerceawra.entity.EstadoPublicacion;

/**
 * Fila del listado de productos del panel.
 *
 * Lleva el estado de publicacion y la disponibilidad por separado: son dos
 * cosas distintas y el panel las muestra en dos insignias.
 */
public record ProductoAdminResumenResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Long categoriaId,
        String categoriaNombre,
        EstadoPublicacion estadoPublicacion,
        long stockTotal,
        DisponibilidadProducto disponibilidad,
        LocalDateTime fechaCreacion) {
}
