package com.awra.co.ecommerceawra.dto;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEnSegundos) {
}
