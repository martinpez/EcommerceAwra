package com.awra.co.ecommerceawra.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.EdicionRequest;
import com.awra.co.ecommerceawra.dto.EdicionResponse;
import com.awra.co.ecommerceawra.entity.EdicionEntity;
import com.awra.co.ecommerceawra.entity.EstadoPublicacion;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.repository.EdicionRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;

/**
 * Ediciones de la Home: la curarquia que decide que se muestra.
 *
 * Una edicion puede apuntar a un producto en cualquier estado, incluso en
 * borrador. Asi el administrador prepara todo y publica el producto al
 * final. La web solo muestra las ediciones activas cuyo producto esta
 * publicado.
 */
@Service
public class EdicionService {

    private final EdicionRepository edicionRepository;
    private final ProductoRepository productoRepository;

    public EdicionService(EdicionRepository edicionRepository, ProductoRepository productoRepository) {
        this.edicionRepository = edicionRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<EdicionResponse> listar() {
        return edicionRepository.findTodasConProducto().stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional
    public EdicionResponse crear(EdicionRequest request) {
        ProductoEntity producto = buscarProducto(request.productoId());
        EdicionEntity edicion = new EdicionEntity(
                producto,
                request.eyebrow(),
                request.titulo(),
                request.descripcion(),
                request.orden());
        edicion.setActiva(request.activa());
        return aResponse(edicionRepository.save(edicion));
    }

    @Transactional
    public EdicionResponse actualizar(Long id, EdicionRequest request) {
        EdicionEntity edicion = buscarEntidad(id);
        ProductoEntity producto = buscarProducto(request.productoId());
        edicion.setProducto(producto);
        edicion.setEyebrow(request.eyebrow());
        edicion.setTitulo(request.titulo());
        edicion.setDescripcion(request.descripcion());
        edicion.setOrden(request.orden());
        edicion.setActiva(request.activa());
        return aResponse(edicion);
    }

    private ProductoEntity buscarProducto(Long productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", productoId));
    }

    private EdicionEntity buscarEntidad(Long id) {
        return edicionRepository.findByIdConProducto(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Edición", id));
    }

    private EdicionResponse aResponse(EdicionEntity edicion) {
        ProductoEntity producto = edicion.getProducto();
        boolean visible = edicion.isActiva() && producto.getEstadoPublicacion() == EstadoPublicacion.PUBLICADO;
        return new EdicionResponse(
                edicion.getId(),
                producto.getId(),
                producto.getNombre(),
                producto.getEstadoPublicacion(),
                edicion.getEyebrow(),
                edicion.getTitulo(),
                edicion.getDescripcion(),
                edicion.getOrden(),
                edicion.isActiva(),
                visible);
    }
}
