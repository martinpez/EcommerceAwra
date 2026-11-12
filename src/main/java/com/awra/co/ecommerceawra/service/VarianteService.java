package com.awra.co.ecommerceawra.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.VarianteRequest;
import com.awra.co.ecommerceawra.dto.VarianteResponse;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.exception.ReglaNegocioException;
import com.awra.co.ecommerceawra.repository.ProductoRepository;
import com.awra.co.ecommerceawra.repository.VarianteRepository;

@Service
public class VarianteService {

    private final VarianteRepository varianteRepository;
    private final ProductoRepository productoRepository;

    public VarianteService(VarianteRepository varianteRepository, ProductoRepository productoRepository) {
        this.varianteRepository = varianteRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public VarianteResponse crear(Long productoId, VarianteRequest request) {
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", productoId));
        String sku = request.sku().trim();
        if (varianteRepository.existsBySku(sku)) {
            throw new ReglaNegocioException("Ya existe una variante con el SKU '" + sku + "'");
        }
        VarianteEntity variante = varianteRepository.save(new VarianteEntity(
                producto,
                request.talla().trim(),
                request.color().trim(),
                sku,
                request.stockMinimo()));
        return aResponse(variante);
    }

    @Transactional
    public VarianteResponse actualizar(Long id, VarianteRequest request) {
        VarianteEntity variante = buscarEntidad(id);
        String sku = request.sku().trim();
        if (varianteRepository.existsBySkuAndIdNot(sku, id)) {
            throw new ReglaNegocioException("Ya existe una variante con el SKU '" + sku + "'");
        }
        variante.setTalla(request.talla().trim());
        variante.setColor(request.color().trim());
        variante.setSku(sku);
        variante.setStockMinimo(request.stockMinimo());
        return aResponse(variante);
    }

    @Transactional
    public void eliminar(Long id) {
        VarianteEntity variante = buscarEntidad(id);
        varianteRepository.delete(variante);
        varianteRepository.flush();
    }

    private VarianteEntity buscarEntidad(Long id) {
        return varianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Variante", id));
    }

    private VarianteResponse aResponse(VarianteEntity variante) {
        return new VarianteResponse(
                variante.getId(),
                variante.getProducto().getId(),
                variante.getTalla(),
                variante.getColor(),
                variante.getSku(),
                variante.getStock(),
                variante.getStockMinimo());
    }
}
