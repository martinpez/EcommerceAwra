package com.awra.co.ecommerceawra.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.awra.co.ecommerceawra.entity.EstadoPublicacion;

/** Proyeccion de la consulta del listado de administracion. */
public record ProductoAdminResumen(
        Long productoId,
        String nombre,
        BigDecimal precio,
        Long categoriaId,
        String categoriaNombre,
        EstadoPublicacion estadoPublicacion,
        Long stockTotal,
        LocalDateTime fechaCreacion) {
}
