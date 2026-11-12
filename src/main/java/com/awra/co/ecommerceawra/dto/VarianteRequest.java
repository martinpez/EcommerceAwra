package com.awra.co.ecommerceawra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record VarianteRequest(
        @NotBlank(message = "La talla es obligatoria")
        @Size(max = 20, message = "La talla no puede superar 20 caracteres")
        String talla,

        @NotBlank(message = "El color es obligatorio")
        @Size(max = 40, message = "El color no puede superar 40 caracteres")
        String color,

        @NotBlank(message = "El SKU es obligatorio")
        @Size(max = 50, message = "El SKU no puede superar 50 caracteres")
        String sku,

        @NotNull(message = "El stock mínimo es obligatorio")
        @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
        Integer stockMinimo) {
}
