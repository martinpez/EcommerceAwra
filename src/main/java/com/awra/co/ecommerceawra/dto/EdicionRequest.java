package com.awra.co.ecommerceawra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EdicionRequest(
        @NotNull(message = "El producto de la edición es obligatorio")
        Long productoId,

        @Size(max = 60, message = "El eyebrow no puede superar 60 caracteres")
        String eyebrow,

        @Size(max = 120, message = "El título no puede superar 120 caracteres")
        String titulo,

        @Size(max = 600, message = "La descripción no puede superar 600 caracteres")
        String descripcion,

        @Min(value = 0, message = "El orden no puede ser negativo")
        int orden,

        boolean activa) {
}
