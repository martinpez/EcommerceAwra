package com.awra.co.ecommerceawra.dto;

import java.time.LocalDateTime;

import com.awra.co.ecommerceawra.entity.TipoMovimiento;

public record MovimientoResponse(
        Long id,
        Long varianteId,
        TipoMovimiento tipo,
        int cantidad,
        int stockResultante,
        String motivo,
        LocalDateTime fechaMovimiento) {
}
