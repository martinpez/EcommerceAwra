package com.awra.co.ecommerceawra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrdenImagenRequest(
        @NotNull(message = "El orden es obligatorio")
        @Min(value = 1, message = "El orden debe ser mayor o igual que 1")
        Integer orden) {
}
