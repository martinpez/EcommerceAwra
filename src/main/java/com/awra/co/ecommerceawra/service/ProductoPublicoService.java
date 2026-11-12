package com.awra.co.ecommerceawra.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.CategoriaPublicaResponse;
import com.awra.co.ecommerceawra.dto.DisponibilidadProducto;
import com.awra.co.ecommerceawra.dto.ImagenPublicaResponse;
import com.awra.co.ecommerceawra.dto.ProductoPublicadoResumen;
import com.awra.co.ecommerceawra.dto.ProductoPublicoDetalleResponse;
import com.awra.co.ecommerceawra.dto.ProductoPublicoResumenResponse;
import com.awra.co.ecommerceawra.dto.VariantePublicaResponse;
import com.awra.co.ecommerceawra.entity.EstadoPublicacion;
import com.awra.co.ecommerceawra.entity.ImagenProductoEntity;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.repository.CategoriaRepository;
import com.awra.co.ecommerceawra.repository.ImagenProductoRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;
import com.awra.co.ecommerceawra.repository.VarianteRepository;

@Service
public class ProductoPublicoService {

    private final ProductoRepository productoRepository;
    private final VarianteRepository varianteRepository;
    private final ImagenProductoRepository imagenRepository;
    private final CategoriaRepository categoriaRepository;
    private final String etiquetaAgotado;

    public ProductoPublicoService(ProductoRepository productoRepository,
            VarianteRepository varianteRepository,
            ImagenProductoRepository imagenRepository,
            CategoriaRepository categoriaRepository,
            @Value("${catalogo.etiqueta-agotado:Agotado}") String etiquetaAgotado) {
        this.productoRepository = productoRepository;
        this.varianteRepository = varianteRepository;
        this.imagenRepository = imagenRepository;
        this.categoriaRepository = categoriaRepository;
        this.etiquetaAgotado = etiquetaAgotado;
    }

    @Transactional(readOnly = true)
    public PagedModel<ProductoPublicoResumenResponse> listar(Long categoriaId, Pageable pageable) {
        Page<ProductoPublicadoResumen> pagina = categoriaId == null
                ? productoRepository.buscarPublicados(pageable)
                : productoRepository.buscarPublicadosPorCategoria(categoriaId, pageable);

        Map<Long, ImagenProductoEntity> principales = imagenesPrincipales(pagina.getContent());
        return new PagedModel<>(pagina.map(resumen -> aResumen(resumen, principales)));
    }

    /** Categorias activas del catalogo, para pintar los filtros de la tienda. */
    @Transactional(readOnly = true)
    public List<CategoriaPublicaResponse> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(categoria -> new CategoriaPublicaResponse(categoria.getId(), categoria.getNombre()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoPublicoDetalleResponse obtener(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .filter(p -> p.getEstadoPublicacion() == EstadoPublicacion.PUBLICADO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        List<VarianteEntity> variantes = varianteRepository.findByProducto_Id(id);
        long stockTotal = variantes.stream().mapToLong(VarianteEntity::getStock).sum();
        DisponibilidadProducto disponibilidad = DisponibilidadProducto.desdeStock(stockTotal);

        return new ProductoPublicoDetalleResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                disponibilidad,
                etiquetaPara(disponibilidad),
                variantes.stream().map(this::aVariantePublica).toList(),
                imagenRepository.findByProducto_IdOrderByOrdenAsc(id).stream().map(this::aImagenPublica).toList());
    }

    /**
     * Foto principal de cada producto de la pagina.
     *
     * Se eligen en una sola consulta para toda la pagina. Gana la que este
     * marcada como principal; si ninguna lo esta, gana la de menor orden, que
     * es la que el administrador puso la primera.
     */
    private Map<Long, ImagenProductoEntity> imagenesPrincipales(List<ProductoPublicadoResumen> resumenes) {
        if (resumenes.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = resumenes.stream().map(ProductoPublicadoResumen::productoId).toList();
        Map<Long, ImagenProductoEntity> porProducto = new HashMap<>();
        for (ImagenProductoEntity imagen : imagenRepository.findByProducto_IdInOrderByOrdenAsc(ids)) {
            Long productoId = imagen.getProducto().getId();
            ImagenProductoEntity actual = porProducto.get(productoId);
            if (actual == null || (imagen.isEsPrincipal() && !actual.isEsPrincipal())) {
                porProducto.put(productoId, imagen);
            }
        }
        return porProducto;
    }

    private ProductoPublicoResumenResponse aResumen(ProductoPublicadoResumen resumen,
            Map<Long, ImagenProductoEntity> principales) {
        DisponibilidadProducto disponibilidad = DisponibilidadProducto.desdeStock(resumen.stockTotal());
        ImagenProductoEntity principal = principales.get(resumen.productoId());
        return new ProductoPublicoResumenResponse(
                resumen.productoId(),
                resumen.nombre(),
                resumen.precio(),
                resumen.categoriaId(),
                resumen.categoriaNombre(),
                disponibilidad,
                etiquetaPara(disponibilidad),
                principal == null ? null : aImagenPublica(principal));
    }

    private VariantePublicaResponse aVariantePublica(VarianteEntity variante) {
        return new VariantePublicaResponse(
                variante.getTalla(),
                variante.getColor(),
                variante.getStock() > 0);
    }

    private ImagenPublicaResponse aImagenPublica(ImagenProductoEntity imagen) {
        return new ImagenPublicaResponse(imagen.getUrl(), imagen.isEsPrincipal(), imagen.getOrden());
    }

    private String etiquetaPara(DisponibilidadProducto disponibilidad) {
        return disponibilidad == DisponibilidadProducto.SIN_STOCK ? etiquetaAgotado : null;
    }
}
