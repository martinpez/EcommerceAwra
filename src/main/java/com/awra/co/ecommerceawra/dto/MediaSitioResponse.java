package com.awra.co.ecommerceawra.dto;

import com.awra.co.ecommerceawra.entity.TipoMedia;

public record MediaSitioResponse(
        Long id,
        Long seccionId,
        String seccionClave,
        TipoMedia tipo,
        String url,
        String publicId,
        String alt,
        Integer ancho,
        Integer alto,
        int orden,
        boolean activa) {
}
