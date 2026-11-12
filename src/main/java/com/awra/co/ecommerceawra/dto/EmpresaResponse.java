package com.awra.co.ecommerceawra.dto;

import java.time.LocalDateTime;

public record EmpresaResponse(
        Long id,
        String nombre,
        String slogan,
        String descripcion,
        String whatsapp,
        String telefono,
        String email,
        String direccion,
        String ciudad,
        String pais,
        String instagram,
        String tiktok,
        String facebook,
        String youtube,
        String horario,
        LocalDateTime fechaActualizacion) {
}
