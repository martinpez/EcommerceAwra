package com.awra.co.ecommerceawra.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.DisponibilidadProducto;
import com.awra.co.ecommerceawra.dto.EdicionPublicaResponse;
import com.awra.co.ecommerceawra.dto.EmpresaResponse;
import com.awra.co.ecommerceawra.dto.MediaSitioResponse;
import com.awra.co.ecommerceawra.dto.SeccionSitioResponse;
import com.awra.co.ecommerceawra.dto.SitioPublicoResponse;
import com.awra.co.ecommerceawra.dto.VideoSitioResponse;
import com.awra.co.ecommerceawra.entity.EdicionEntity;
import com.awra.co.ecommerceawra.entity.ImagenProductoEntity;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.repository.EdicionRepository;
import com.awra.co.ecommerceawra.repository.ImagenProductoRepository;
import com.awra.co.ecommerceawra.repository.VarianteRepository;

/**
 * Contenido publico del sitio.
 *
 * Aqui no hay nada que el administrador pueda cambiar a mano: solo se
 * devuelve lo que esta activo y, en el caso de las ediciones, lo que ademas
 * tiene su producto publicado.
 */
@Service
public class SitioPublicoService {

    private final EmpresaService empresaService;
    private final SeccionSitioService seccionSitioService;
    private final MediaSitioService mediaSitioService;
    private final VideoSitioService videoSitioService;
    private final EdicionRepository edicionRepository;
    private final VarianteRepository varianteRepository;
    private final ImagenProductoRepository imagenRepository;
    private final String etiquetaAgotado;

    public SitioPublicoService(EmpresaService empresaService,
            SeccionSitioService seccionSitioService,
            MediaSitioService mediaSitioService,
            VideoSitioService videoSitioService,
            EdicionRepository edicionRepository,
            VarianteRepository varianteRepository,
            ImagenProductoRepository imagenRepository,
            @Value("${catalogo.etiqueta-agotado:Agotado}") String etiquetaAgotado) {
        this.empresaService = empresaService;
        this.seccionSitioService = seccionSitioService;
        this.mediaSitioService = mediaSitioService;
        this.videoSitioService = videoSitioService;
        this.edicionRepository = edicionRepository;
        this.varianteRepository = varianteRepository;
        this.imagenRepository = imagenRepository;
        this.etiquetaAgotado = etiquetaAgotado;
    }

    /**
     * Empresa, secciones activas, medios activos y videos activos.
     *
     * La transaccion no es de solo lectura porque la empresa se crea la
     * primera vez que se consulta, si todavia no existe.
     */
    @Transactional
    public SitioPublicoResponse sitio() {
        EmpresaResponse empresa = empresaService.obtener();
        List<SeccionSitioResponse> secciones = seccionSitioService.listarActivas();
        List<MediaSitioResponse> media = mediaSitioService.listarActivos();
        List<VideoSitioResponse> videos = videoSitioService.listarActivos();
        return new SitioPublicoResponse(empresa, secciones, media, videos);
    }

    /** Ediciones visibles: activas y con el producto en estado PUBLICADO. */
    @Transactional(readOnly = true)
    public List<EdicionPublicaResponse> ediciones() {
        return edicionRepository.findPublicas().stream()
                .map(this::aEdicion)
                .toList();
    }

    private EdicionPublicaResponse aEdicion(EdicionEntity edicion) {
        ProductoEntity producto = edicion.getProducto();
        List<ImagenProductoEntity> imagenes = imagenRepository.findByProducto_IdOrderByOrdenAsc(producto.getId());

        long stockTotal = varianteRepository.findByProducto_Id(producto.getId()).stream()
                .mapToLong(VarianteEntity::getStock)
                .sum();
        DisponibilidadProducto disponibilidad = DisponibilidadProducto.desdeStock(stockTotal);

        BigDecimal precio = producto.getPrecio();
        return new EdicionPublicaResponse(
                edicion.getId(),
                edicion.getEyebrow(),
                edicion.getTitulo(),
                edicion.getDescripcion(),
                edicion.getOrden(),
                producto.getId(),
                producto.getNombre(),
                precio,
                imagenes.isEmpty() ? null : imagenes.get(0).getUrl(),
                imagenes.size() < 2 ? null : imagenes.get(1).getUrl(),
                disponibilidad,
                disponibilidad == DisponibilidadProducto.SIN_STOCK ? etiquetaAgotado : null);
    }
}
