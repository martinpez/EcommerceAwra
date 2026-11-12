package com.awra.co.ecommerceawra.dto;

import com.awra.co.ecommerceawra.entity.TipoMovimiento;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MovimientoRequest(
        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipo,

        @NotNull(message = "La cantidad es obligatoria")
        Integer cantidad,

        @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
        String motivo) {

    @AssertTrue(message = "ENTRADA y VENTA requieren una cantidad mayor que cero; AJUSTE requiere una cantidad distinta de cero")
    public boolean isCantidadValida() {
        if (tipo == null || cantidad == null) {
            return true;
        }
        return tipo == TipoMovimiento.AJUSTE ? cantidad != 0 : cantidad > 0;
    }
}
