package com.awra.co.ecommerceawra.dto;

public record VideoSitioResponse(
        Long id,
        Long seccionId,
        String seccionClave,
        String titulo,
        String etiqueta,
        String url,
        String publicId,
        String posterUrl,
        Integer ancho,
        Integer alto,
        Integer duracionSeg,
        int orden,
        boolean activa) {
}
