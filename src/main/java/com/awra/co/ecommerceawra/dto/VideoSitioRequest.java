package com.awra.co.ecommerceawra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VideoSitioRequest(
        /** Clave de la seccion del video. Por defecto, "MOVIMIENTO". */
        @Size(max = 40, message = "La clave de sección no puede superar 40 caracteres")
        String seccionClave,

        @NotBlank(message = "El título del video es obligatorio")
        @Size(max = 120, message = "El título no puede superar 120 caracteres")
        String titulo,

        @Size(max = 120, message = "La etiqueta no puede superar 120 caracteres")
        String etiqueta,

        @Min(value = 0, message = "El orden no puede ser negativo")
        int orden,

        boolean activa) {
}
