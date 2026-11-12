package com.awra.co.ecommerceawra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.awra.co.ecommerceawra.entity.AlertaStockEntity;
import com.awra.co.ecommerceawra.entity.TipoAlerta;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.repository.AlertaStockRepository;

@ExtendWith(MockitoExtension.class)
class AlertaStockServiceTest {

    @Mock
    private AlertaStockRepository alertaRepository;

    @InjectMocks
    private AlertaStockService alertaStockService;

    @Test
    void cruzarHaciaAbajoElMinimoCreaAlertaStockBajo() {
        VarianteEntity variante = variante(2, 2);
        when(alertaRepository.findFirstByVariante_IdAndFechaResolucionIsNull(any())).thenReturn(Optional.empty());

        alertaStockService.evaluar(variante, 3);

        ArgumentCaptor<AlertaStockEntity> captor = ArgumentCaptor.forClass(AlertaStockEntity.class);
        verify(alertaRepository).save(captor.capture());
        assertThat(captor.getValue().getTipo()).isEqualTo(TipoAlerta.STOCK_BAJO);
        assertThat(captor.getValue().getStockAlMomento()).isEqualTo(2);
    }

    @Test
    void ventaQueNoCruzaElMinimoNoCreaAlerta() {
        VarianteEntity variante = variante(2, 4);
        when(alertaRepository.findFirstByVariante_IdAndFechaResolucionIsNull(any())).thenReturn(Optional.empty());

        alertaStockService.evaluar(variante, 5);

        verify(alertaRepository, never()).save(any());
    }

    @Test
    void llegarACeroEscalaLaAlertaActivaSinDuplicarla() {
        VarianteEntity variante = variante(2, 0);
        AlertaStockEntity activa = new AlertaStockEntity(variante, TipoAlerta.STOCK_BAJO, 2);
        when(alertaRepository.findFirstByVariante_IdAndFechaResolucionIsNull(any())).thenReturn(Optional.of(activa));

        alertaStockService.evaluar(variante, 1);

        assertThat(activa.getTipo()).isEqualTo(TipoAlerta.AGOTADO);
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void reponerPorEncimaDelMinimoResuelveLaAlertaActiva() {
        VarianteEntity variante = variante(2, 6);
        AlertaStockEntity activa = new AlertaStockEntity(variante, TipoAlerta.STOCK_BAJO, 2);
        when(alertaRepository.findFirstByVariante_IdAndFechaResolucionIsNull(any())).thenReturn(Optional.of(activa));

        alertaStockService.evaluar(variante, 1);

        assertThat(activa.getFechaResolucion()).isNotNull();
    }

    private static VarianteEntity variante(int minimo, int stock) {
        VarianteEntity variante = new VarianteEntity(null, "M", "Negro", "SKU-1", minimo);
        variante.modificarStock(stock);
        return variante;
    }
}
