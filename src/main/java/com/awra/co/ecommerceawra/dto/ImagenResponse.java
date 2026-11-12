package com.awra.co.ecommerceawra.dto;

public record ImagenResponse(
        Long id,
        String url,
        boolean principal,
        int orden) {
}
