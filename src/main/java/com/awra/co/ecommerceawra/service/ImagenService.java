package com.awra.co.ecommerceawra.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.awra.co.ecommerceawra.dto.ImagenResponse;
import com.awra.co.ecommerceawra.entity.ImagenProductoEntity;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.media.ValidacionArchivo;
import com.awra.co.ecommerceawra.repository.ImagenProductoRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;

@Service
public class ImagenService {

    private final ProductoRepository productoRepository;
    private final ImagenProductoRepository imagenRepository;
    private final CloudinaryService cloudinaryService;
    private final TransactionTemplate transactionTemplate;

    public ImagenService(ProductoRepository productoRepository,
            ImagenProductoRepository imagenRepository,
            CloudinaryService cloudinaryService,
            TransactionTemplate transactionTemplate) {
        this.productoRepository = productoRepository;
        this.imagenRepository = imagenRepository;
        this.cloudinaryService = cloudinaryService;
        this.transactionTemplate = transactionTemplate;
    }

    public ImagenResponse subir(Long productoId, MultipartFile archivo) {
        byte[] contenido = leerImagenValida(archivo);
        buscarProducto(productoId);

        CloudinaryService.ImagenSubida subida = cloudinaryService.subir(contenido);
        try {
            return transactionTemplate.execute(estado -> guardar(productoId, subida));
        } catch (RuntimeException ex) {
            cloudinaryService.eliminarSilencioso(subida.publicId());
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public List<ImagenResponse> listar(Long productoId) {
        buscarProducto(productoId);
        return imagenRepository.findByProducto_IdOrderByOrdenAsc(productoId).stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional
    public ImagenResponse marcarPrincipal(Long productoId, Long imagenId) {
        ImagenProductoEntity objetivo = buscarImagen(productoId, imagenId);
        imagenRepository.findByProducto_IdOrderByOrdenAsc(productoId)
                .forEach(imagen -> imagen.setEsPrincipal(false));
        imagenRepository.flush();

        objetivo.setEsPrincipal(true);
        imagenRepository.flush();
        return aResponse(objetivo);
    }

    @Transactional
    public ImagenResponse actualizarOrden(Long productoId, Long imagenId, int orden) {
        ImagenProductoEntity imagen = buscarImagen(productoId, imagenId);
        imagen.setOrden(orden);
        return aResponse(imagen);
    }

    @Transactional
    public void eliminar(Long productoId, Long imagenId) {
        ImagenProductoEntity imagen = buscarImagen(productoId, imagenId);
        boolean eraPrincipal = imagen.isEsPrincipal();
        imagenRepository.delete(imagen);
        imagenRepository.flush();

        if (eraPrincipal) {
            imagenRepository.findByProducto_IdOrderByOrdenAsc(productoId).stream()
                    .findFirst()
                    .ifPresent(siguiente -> {
                        siguiente.setEsPrincipal(true);
                        imagenRepository.flush();
                    });
        }
    }

    private ImagenResponse guardar(Long productoId, CloudinaryService.ImagenSubida subida) {
        ProductoEntity producto = buscarProducto(productoId);
        List<ImagenProductoEntity> actuales = imagenRepository.findByProducto_IdOrderByOrdenAsc(productoId);
        int orden = actuales.stream().mapToInt(ImagenProductoEntity::getOrden).max().orElse(0) + 1;

        ImagenProductoEntity imagen = imagenRepository.save(new ImagenProductoEntity(
                producto,
                subida.url(),
                subida.publicId(),
                orden,
                actuales.isEmpty()));
        return aResponse(imagen);
    }

    private byte[] leerImagenValida(MultipartFile archivo) {
        return ValidacionArchivo.leerImagen(archivo);
    }

    private ProductoEntity buscarProducto(Long productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", productoId));
    }

    private ImagenProductoEntity buscarImagen(Long productoId, Long imagenId) {
        return imagenRepository.findById(imagenId)
                .filter(imagen -> imagen.getProducto().getId().equals(productoId))
                .orElseThrow(() -> new RecursoNoEncontradoException("Imagen", imagenId));
    }

    private ImagenResponse aResponse(ImagenProductoEntity imagen) {
        return new ImagenResponse(imagen.getId(), imagen.getUrl(), imagen.isEsPrincipal(), imagen.getOrden());
    }
}
