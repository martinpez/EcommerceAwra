package com.awra.co.ecommerceawra.dto;

import com.awra.co.ecommerceawra.entity.EstadoPublicacion;

import jakarta.validation.constraints.NotNull;

public record PublicacionRequest(
        @NotNull(message = "El estado de publicación es obligatorio")
        EstadoPublicacion estado) {
}
