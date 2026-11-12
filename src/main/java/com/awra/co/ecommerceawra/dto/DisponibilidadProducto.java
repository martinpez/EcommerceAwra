package com.awra.co.ecommerceawra.dto;

public enum DisponibilidadProducto {
    DISPONIBLE,
    SIN_STOCK;

    public static DisponibilidadProducto desdeStock(long stockTotal) {
        return stockTotal > 0 ? DISPONIBLE : SIN_STOCK;
    }
}
