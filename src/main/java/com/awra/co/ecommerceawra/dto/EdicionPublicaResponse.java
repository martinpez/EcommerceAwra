package com.awra.co.ecommerceawra.dto;

import java.math.BigDecimal;

public record EdicionPublicaResponse(
        Long id,
        String eyebrow,
        String titulo,
        String descripcion,
        int orden,
        Long productoId,
        String productoNombre,
        BigDecimal precio,
        /** Primera imagen del producto (orden 1). Es el frente del buzo. */
        String imagenFrente,
        /** Segunda imagen del producto (orden 2). Es la espalda. Puede venir nula. */
        String imagenEspalda,
        DisponibilidadProducto disponibilidad,
        String etiqueta) {
}
