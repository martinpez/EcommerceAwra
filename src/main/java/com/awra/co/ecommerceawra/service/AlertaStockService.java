package com.awra.co.ecommerceawra.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.AlertaStockResponse;
import com.awra.co.ecommerceawra.entity.AlertaStockEntity;
import com.awra.co.ecommerceawra.entity.TipoAlerta;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.repository.AlertaStockRepository;

@Service
public class AlertaStockService {

    private final AlertaStockRepository alertaRepository;

    public AlertaStockService(AlertaStockRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertaStockResponse> listar(Boolean leida) {
        List<AlertaStockEntity> alertas = leida == null
                ? alertaRepository.buscarVisibles()
                : alertaRepository.buscarVisiblesPorLeida(leida);
        return alertas.stream().map(this::aResponse).toList();
    }

    @Transactional
    public AlertaStockResponse marcarLeida(Long id) {
        AlertaStockEntity alerta = alertaRepository.buscarVisiblePorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alerta", id));
        alerta.marcarLeida();
        return aResponse(alerta);
    }

    @Transactional
    public void evaluar(VarianteEntity variante, int stockAnterior) {
        int stockActual = variante.getStock();
        int minimo = variante.getStockMinimo();
        Optional<AlertaStockEntity> activa = alertaRepository
                .findFirstByVariante_IdAndFechaResolucionIsNull(variante.getId());

        if (stockActual > minimo) {
            activa.ifPresent(AlertaStockEntity::resolver);
            return;
        }

        TipoAlerta tipo = stockActual == 0 ? TipoAlerta.AGOTADO : TipoAlerta.STOCK_BAJO;
        if (activa.isPresent()) {
            activa.get().escalarA(tipo, stockActual);
            return;
        }

        boolean cruzaHaciaAbajo = stockAnterior > minimo;
        if (cruzaHaciaAbajo) {
            alertaRepository.save(new AlertaStockEntity(variante, tipo, stockActual));
        }
    }

    private AlertaStockResponse aResponse(AlertaStockEntity alerta) {
        VarianteEntity variante = alerta.getVariante();
        return new AlertaStockResponse(
                alerta.getId(),
                variante.getId(),
                variante.getProducto().getNombre(),
                variante.getTalla(),
                variante.getColor(),
                variante.getSku(),
                alerta.getTipo(),
                alerta.getStockAlMomento(),
                alerta.isLeida(),
                alerta.getFechaCreacion(),
                alerta.getFechaResolucion());
    }
}
