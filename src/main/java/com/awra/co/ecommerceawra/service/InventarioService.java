package com.awra.co.ecommerceawra.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.DisponibilidadProducto;
import com.awra.co.ecommerceawra.dto.InventarioProductoResponse;
import com.awra.co.ecommerceawra.dto.InventarioProductoResumen;
import com.awra.co.ecommerceawra.repository.ProductoRepository;

@Service
public class InventarioService {

    private final ProductoRepository productoRepository;

    public InventarioService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public PagedModel<InventarioProductoResponse> listar(Pageable pageable) {
        Page<InventarioProductoResponse> pagina = productoRepository.buscarInventario(pageable)
                .map(this::aResponse);
        return new PagedModel<>(pagina);
    }

    private InventarioProductoResponse aResponse(InventarioProductoResumen resumen) {
        long stockTotal = resumen.stockTotal();
        return new InventarioProductoResponse(
                resumen.productoId(),
                resumen.nombre(),
                resumen.categoriaId(),
                resumen.categoriaNombre(),
                stockTotal,
                DisponibilidadProducto.desdeStock(stockTotal));
    }
}
