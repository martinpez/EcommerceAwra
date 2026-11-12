package com.awra.co.ecommerceawra.dto;

/**
 * Categoria para el catalogo publico.
 *
 * Es un DTO propio, separado del de administracion, para que el contrato
 * publico no dependa de como se modifique el panel.
 */
public record CategoriaPublicaResponse(
        Long id,
        String nombre) {
}
