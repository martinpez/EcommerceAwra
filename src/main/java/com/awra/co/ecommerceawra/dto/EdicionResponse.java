package com.awra.co.ecommerceawra.dto;

import com.awra.co.ecommerceawra.entity.EstadoPublicacion;

public record EdicionResponse(
        Long id,
        Long productoId,
        String productoNombre,
        EstadoPublicacion productoEstado,
        String eyebrow,
        String titulo,
        String descripcion,
        int orden,
        boolean activa,
        /** true solo si esta activa y su producto esta publicado: es lo que la web muestra. */
        boolean visible) {
}
