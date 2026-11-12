package com.awra.co.ecommerceawra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SeccionSitioRequest(
        @NotBlank(message = "La clave de la sección es obligatoria")
        @Pattern(regexp = "^[A-Z][A-Z_]{1,39}$", message = "La clave solo admite mayúsculas y guion bajo")
        String clave,

        @Size(max = 80, message = "El eyebrow no puede superar 80 caracteres")
        String eyebrow,

        @Size(max = 200, message = "El título no puede superar 200 caracteres")
        String titulo,

        @Size(max = 2000, message = "El cuerpo no puede superar 2000 caracteres")
        String cuerpo,

        @Min(value = 0, message = "El orden no puede ser negativo")
        int orden,

        boolean activa) {
}
