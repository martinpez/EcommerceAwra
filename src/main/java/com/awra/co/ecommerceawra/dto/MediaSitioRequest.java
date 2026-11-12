package com.awra.co.ecommerceawra.dto;

import com.awra.co.ecommerceawra.entity.TipoMedia;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MediaSitioRequest(
        /** Clave de la seccion a la que pertenece el medio. Opcional: el logo no pertenece a ninguna. */
        @Size(max = 40, message = "La clave de sección no puede superar 40 caracteres")
        String seccionClave,

        @NotNull(message = "El tipo de medio es obligatorio")
        TipoMedia tipo,

        @Size(max = 200, message = "El texto alternativo no puede superar 200 caracteres")
        String alt,

        @Min(value = 0, message = "El orden no puede ser negativo")
        int orden,

        boolean activa) {
}
