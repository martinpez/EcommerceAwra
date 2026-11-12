package com.awra.co.ecommerceawra.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.DisponibilidadProducto;
import com.awra.co.ecommerceawra.dto.ProductoActualizarRequest;
import com.awra.co.ecommerceawra.dto.ProductoAdminResumen;
import com.awra.co.ecommerceawra.dto.ProductoAdminResumenResponse;
import com.awra.co.ecommerceawra.dto.PublicacionRequest;
import com.awra.co.ecommerceawra.dto.ProductoCrearRequest;
import com.awra.co.ecommerceawra.dto.ProductoResponse;
import com.awra.co.ecommerceawra.dto.VarianteRequest;
import com.awra.co.ecommerceawra.dto.VarianteResponse;
import com.awra.co.ecommerceawra.entity.CategoriaEntity;
import com.awra.co.ecommerceawra.entity.EstadoPublicacion;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.exception.ReglaNegocioException;
import com.awra.co.ecommerceawra.repository.CategoriaRepository;
import com.awra.co.ecommerceawra.repository.EdicionRepository;
import com.awra.co.ecommerceawra.repository.ImagenProductoRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;
import com.awra.co.ecommerceawra.repository.VarianteRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final VarianteRepository varianteRepository;
    private final ImagenProductoRepository imagenRepository;
    private final EdicionRepository edicionRepository;

    public ProductoService(ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            VarianteRepository varianteRepository,
            ImagenProductoRepository imagenRepository,
            EdicionRepository edicionRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.varianteRepository = varianteRepository;
        this.imagenRepository = imagenRepository;
        this.edicionRepository = edicionRepository;
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        ProductoEntity producto = buscarEntidad(id);
        return aResponse(producto, varianteRepository.findByProducto_Id(id));
    }

    /**
     * Listado del panel, con filtros opcionales por estado, categoria y
     * busqueda por nombre.
     */
    @Transactional(readOnly = true)
    public PagedModel<ProductoAdminResumenResponse> listarParaAdmin(EstadoPublicacion estado, Long categoriaId,
            String busqueda, Pageable pageable) {
        String texto = busqueda == null || busqueda.isBlank() ? null : busqueda.trim();
        Page<ProductoAdminResumen> pagina = productoRepository.buscarParaAdmin(estado, categoriaId, texto, pageable);
        return new PagedModel<>(pagina.map(this::aResumenAdmin));
    }

    @Transactional
    public ProductoResponse crear(ProductoCrearRequest request) {
        CategoriaEntity categoria = buscarCategoria(request.categoriaId());
        validarSkusNuevos(request.variantes());

        ProductoEntity producto = productoRepository.save(new ProductoEntity(
                request.nombre().trim(),
                request.descripcion(),
                request.precio(),
                categoria));

        List<VarianteEntity> variantes = request.variantes().stream()
                .map(v -> varianteRepository.save(new VarianteEntity(
                        producto,
                        v.talla().trim(),
                        v.color().trim(),
                        v.sku().trim(),
                        v.stockMinimo())))
                .toList();

        return aResponse(producto, variantes);
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoActualizarRequest request) {
        ProductoEntity producto = buscarEntidad(id);
        CategoriaEntity categoria = buscarCategoria(request.categoriaId());

        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setCategoria(categoria);

        return aResponse(producto, varianteRepository.findByProducto_Id(id));
    }

    @Transactional
    public void eliminar(Long id) {
        ProductoEntity producto = buscarEntidad(id);
        desactivarEdiciones(id);
        varianteRepository.deleteAll(varianteRepository.findByProducto_Id(id));
        imagenRepository.deleteAll(imagenRepository.findByProducto_IdOrderByOrdenAsc(id));
        productoRepository.delete(producto);
        productoRepository.flush();
    }

    @Transactional
    public ProductoResponse cambiarPublicacion(Long id, PublicacionRequest request) {
        if (request.estado() == EstadoPublicacion.ELIMINADO) {
            throw new ReglaNegocioException("Para eliminar un producto usa DELETE /api/admin/productos/{id}");
        }
        ProductoEntity producto = buscarEntidad(id);
        if (request.estado() == EstadoPublicacion.PUBLICADO) {
            validarQueSePuedePublicar(id);
        }
        producto.setEstadoPublicacion(request.estado());
        return aResponse(producto, varianteRepository.findByProducto_Id(id));
    }

    /**
     * No se publica un producto incompleto: sin talla ni con stock 0 no hay
     * nada que comprar, y sin foto la Home mostraria una tarjeta rota.
     */
    private void validarQueSePuedePublicar(Long id) {
        if (varianteRepository.findByProducto_Id(id).isEmpty()) {
            throw new ReglaNegocioException("No se puede publicar un producto sin al menos una talla");
        }
        if (imagenRepository.findByProducto_IdOrderByOrdenAsc(id).isEmpty()) {
            throw new ReglaNegocioException("No se puede publicar un producto sin al menos una imagen");
        }
    }

    /**
     * Al eliminar un producto sus ediciones se desactivan, para no dejar la
     * curaduría de la Home apuntando a un producto inexistente.
     */
    private void desactivarEdiciones(Long productoId) {
        edicionRepository.desactivarPorProducto(productoId);
    }

    private void validarSkusNuevos(List<VarianteRequest> variantes) {
        Set<String> skusDeLaPeticion = new HashSet<>();
        for (VarianteRequest variante : variantes) {
            String sku = variante.sku().trim();
            if (!skusDeLaPeticion.add(sku)) {
                throw new ReglaNegocioException("El SKU '" + sku + "' se repite en la misma petición");
            }
            if (varianteRepository.existsBySku(sku)) {
                throw new ReglaNegocioException("Ya existe una variante con el SKU '" + sku + "'");
            }
        }
    }

    private ProductoEntity buscarEntidad(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
    }

    private CategoriaEntity buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", id));
    }

    private ProductoResponse aResponse(ProductoEntity producto, List<VarianteEntity> variantes) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                producto.getEstadoPublicacion(),
                producto.getFechaCreacion(),
                producto.getFechaActualizacion(),
                variantes.stream().map(this::aVarianteResponse).toList());
    }

    private VarianteResponse aVarianteResponse(VarianteEntity variante) {
        return new VarianteResponse(
                variante.getId(),
                variante.getProducto().getId(),
                variante.getTalla(),
                variante.getColor(),
                variante.getSku(),
                variante.getStock(),
                variante.getStockMinimo());
    }

    private ProductoAdminResumenResponse aResumenAdmin(ProductoAdminResumen resumen) {
        long stockTotal = resumen.stockTotal() == null ? 0 : resumen.stockTotal();
        return new ProductoAdminResumenResponse(
                resumen.productoId(),
                resumen.nombre(),
                resumen.precio(),
                resumen.categoriaId(),
                resumen.categoriaNombre(),
                resumen.estadoPublicacion(),
                stockTotal,
                DisponibilidadProducto.desdeStock(stockTotal),
                resumen.fechaCreacion());
    }
}
