package com.awra.co.ecommerceawra.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.MovimientoRequest;
import com.awra.co.ecommerceawra.dto.MovimientoResponse;
import com.awra.co.ecommerceawra.entity.MovimientoInventarioEntity;
import com.awra.co.ecommerceawra.entity.TipoMovimiento;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.exception.ReglaNegocioException;
import com.awra.co.ecommerceawra.repository.MovimientoInventarioRepository;
import com.awra.co.ecommerceawra.repository.VarianteRepository;

@Service
public class MovimientoService {

    private final VarianteRepository varianteRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final AlertaStockService alertaStockService;

    public MovimientoService(VarianteRepository varianteRepository,
            MovimientoInventarioRepository movimientoRepository,
            AlertaStockService alertaStockService) {
        this.varianteRepository = varianteRepository;
        this.movimientoRepository = movimientoRepository;
        this.alertaStockService = alertaStockService;
    }

    @Transactional
    public MovimientoResponse registrar(Long varianteId, MovimientoRequest request) {
        VarianteEntity variante = buscarVariante(varianteId);
        int delta = calcularDelta(request.tipo(), request.cantidad());

        if (variante.getStock() + delta < 0) {
            throw new ReglaNegocioException("Stock insuficiente: disponible " + variante.getStock()
                    + ", solicitado " + Math.abs(delta));
        }

        int stockAnterior = variante.getStock();
        variante.modificarStock(delta);
        varianteRepository.flush();

        MovimientoInventarioEntity movimiento = movimientoRepository.save(new MovimientoInventarioEntity(
                variante,
                request.tipo(),
                delta,
                variante.getStock(),
                request.motivo()));
        alertaStockService.evaluar(variante, stockAnterior);
        return aResponse(movimiento);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> historial(Long varianteId) {
        buscarVariante(varianteId);
        return movimientoRepository.findByVariante_IdOrderByFechaMovimientoDescIdDesc(varianteId).stream()
                .map(this::aResponse)
                .toList();
    }

    private int calcularDelta(TipoMovimiento tipo, int cantidad) {
        return switch (tipo) {
            case ENTRADA -> cantidad;
            case VENTA -> -cantidad;
            case AJUSTE -> cantidad;
        };
    }

    private VarianteEntity buscarVariante(Long id) {
        return varianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Variante", id));
    }

    private MovimientoResponse aResponse(MovimientoInventarioEntity movimiento) {
        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getVariante().getId(),
                movimiento.getTipo(),
                movimiento.getCantidad(),
                movimiento.getStockResultante(),
                movimiento.getMotivo(),
                movimiento.getFechaMovimiento());
    }
}
