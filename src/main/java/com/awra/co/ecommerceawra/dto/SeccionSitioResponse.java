package com.awra.co.ecommerceawra.dto;

public record SeccionSitioResponse(
        Long id,
        String clave,
        String eyebrow,
        String titulo,
        String cuerpo,
        int orden,
        boolean activa) {
}
