package com.awra.co.ecommerceawra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.awra.co.ecommerceawra.dto.MovimientoRequest;
import com.awra.co.ecommerceawra.entity.MovimientoInventarioEntity;
import com.awra.co.ecommerceawra.entity.TipoMovimiento;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.exception.ReglaNegocioException;
import com.awra.co.ecommerceawra.repository.MovimientoInventarioRepository;
import com.awra.co.ecommerceawra.repository.VarianteRepository;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private VarianteRepository varianteRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private AlertaStockService alertaStockService;

    @InjectMocks
    private MovimientoService movimientoService;

    @Test
    void ventaMayorQueElStockSeRechazaSinTocarNada() {
        VarianteEntity variante = varianteConStock(7);
        when(varianteRepository.findById(1L)).thenReturn(Optional.of(variante));

        assertThatThrownBy(() -> movimientoService.registrar(1L, new MovimientoRequest(TipoMovimiento.VENTA, 20, null)))
                .isInstanceOf(ReglaNegocioException.class);
        assertThat(variante.getStock()).isEqualTo(7);
        verify(movimientoRepository, never()).save(any());
        verify(alertaStockService, never()).evaluar(any(), any(Integer.class));
    }

    @Test
    void ventaValidaDescuentaStockGuardaSignoNegativoYEvaluaAlerta() {
        VarianteEntity variante = varianteConStock(7);
        when(varianteRepository.findById(1L)).thenReturn(Optional.of(variante));
        when(movimientoRepository.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        movimientoService.registrar(1L, new MovimientoRequest(TipoMovimiento.VENTA, 3, "venta"));

        assertThat(variante.getStock()).isEqualTo(4);
        verify(alertaStockService).evaluar(variante, 7);
        verify(movimientoRepository).save(argumentoMovimiento(-3, 4));
    }

    @Test
    void ajusteNegativoCorrigeConSignoContrario() {
        VarianteEntity variante = varianteConStock(5);
        when(varianteRepository.findById(1L)).thenReturn(Optional.of(variante));
        when(movimientoRepository.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        movimientoService.registrar(1L, new MovimientoRequest(TipoMovimiento.AJUSTE, -2, "dañado"));

        assertThat(variante.getStock()).isEqualTo(3);
        verify(movimientoRepository).save(argumentoMovimiento(-2, 3));
    }

    private static VarianteEntity varianteConStock(int stock) {
        VarianteEntity variante = new VarianteEntity(null, "M", "Negro", "SKU-1", 2);
        variante.modificarStock(stock);
        return variante;
    }

    private static MovimientoInventarioEntity argumentoMovimiento(int cantidad, int stockResultante) {
        return org.mockito.ArgumentMatchers.argThat(movimiento ->
                movimiento.getCantidad() == cantidad && movimiento.getStockResultante() == stockResultante);
    }
}
