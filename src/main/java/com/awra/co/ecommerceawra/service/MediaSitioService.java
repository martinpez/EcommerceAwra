package com.awra.co.ecommerceawra.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.awra.co.ecommerceawra.config.CloudinaryProperties;
import com.awra.co.ecommerceawra.dto.MediaSitioRequest;
import com.awra.co.ecommerceawra.dto.MediaSitioResponse;
import com.awra.co.ecommerceawra.entity.MediaSitioEntity;
import com.awra.co.ecommerceawra.entity.SeccionSitioEntity;
import com.awra.co.ecommerceawra.entity.TipoMedia;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.exception.ReglaNegocioException;
import com.awra.co.ecommerceawra.media.ValidacionArchivo;
import com.awra.co.ecommerceawra.repository.MediaSitioRepository;

/**
 * Logo e imagenes de las secciones del sitio.
 *
 * A diferencia de las imagenes de producto, aqui el archivo si se borra de
 * Cloudinary cuando el administrador elimina el medio: el contenido del sitio
 * no tiene historial que conservar.
 */
@Service
public class MediaSitioService {

    private final MediaSitioRepository mediaRepository;
    private final SeccionSitioService seccionSitioService;
    private final CloudinaryService cloudinaryService;
    private final CloudinaryProperties cloudinaryProperties;
    private final TransactionTemplate transactionTemplate;

    public MediaSitioService(MediaSitioRepository mediaRepository,
            SeccionSitioService seccionSitioService,
            CloudinaryService cloudinaryService,
            CloudinaryProperties cloudinaryProperties,
            TransactionTemplate transactionTemplate) {
        this.mediaRepository = mediaRepository;
        this.seccionSitioService = seccionSitioService;
        this.cloudinaryService = cloudinaryService;
        this.cloudinaryProperties = cloudinaryProperties;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional(readOnly = true)
    public List<MediaSitioResponse> listar() {
        return mediaRepository.findAll().stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MediaSitioResponse> listarPorSeccion(String seccionClave) {
        SeccionSitioEntity seccion = seccionSitioService.buscarPorClave(seccionClave);
        return mediaRepository.findBySeccion_IdAndActivaTrueOrderByOrdenAsc(seccion.getId()).stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MediaSitioResponse> listarActivos() {
        return mediaRepository.findAll().stream()
                .filter(MediaSitioEntity::isActiva)
                .map(this::aResponse)
                .toList();
    }

    public MediaSitioResponse subir(MultipartFile archivo, MediaSitioRequest request) {
        byte[] contenido = ValidacionArchivo.leerImagen(archivo);
        SeccionSitioEntity seccion = resolverSeccion(request.seccionClave());

        // El logo se muestra enorme en el hero: necesita mas ancho que una
        // imagen de seccion.
        int ancho = request.tipo() == TipoMedia.LOGO
                ? CloudinaryService.ANCHO_ENTREGA_LOGO
                : CloudinaryService.ANCHO_ENTREGA_IMAGEN;

        CloudinaryService.ImagenSubida subida = cloudinaryService.subir(contenido, cloudinaryProperties.carpetaSitio(), ancho);
        try {
            return transactionTemplate.execute(estado -> guardar(seccion, request, subida));
        } catch (RuntimeException ex) {
            // Compensacion: si no se pudo guardar en la base de datos, el archivo
            // que subimos a Cloudinary se queda huerfano. Se borra.
            cloudinaryService.eliminarSilencioso(subida.publicId());
            throw ex;
        }
    }

    @Transactional
    public MediaSitioResponse actualizar(Long id, MediaSitioRequest request) {
        MediaSitioEntity media = buscarEntidad(id);
        media.setSeccion(resolverSeccion(request.seccionClave()));
        media.setAlt(request.alt());
        media.setOrden(request.orden());
        media.setActiva(request.activa());
        return aResponse(media);
    }

    @Transactional
    public void eliminar(Long id) {
        MediaSitioEntity media = buscarEntidad(id);
        String publicId = media.getPublicId();
        mediaRepository.delete(media);
        mediaRepository.flush();
        cloudinaryService.eliminarSilencioso(publicId);
    }

    private MediaSitioResponse guardar(SeccionSitioEntity seccion, MediaSitioRequest request,
            CloudinaryService.ImagenSubida subida) {
        MediaSitioEntity media = mediaRepository.save(new MediaSitioEntity(
                seccion,
                request.tipo(),
                subida.url(),
                subida.publicId(),
                request.alt(),
                subida.ancho(),
                subida.alto(),
                request.orden()));
        media.setActiva(request.activa());
        return aResponse(media);
    }

    private SeccionSitioEntity resolverSeccion(String seccionClave) {
        if (seccionClave == null || seccionClave.isBlank()) {
            return null;
        }
        return seccionSitioService.buscarPorClave(seccionClave.trim());
    }

    private MediaSitioEntity buscarEntidad(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medio", id));
    }

    private MediaSitioResponse aResponse(MediaSitioEntity media) {
        SeccionSitioEntity seccion = media.getSeccion();
        return new MediaSitioResponse(
                media.getId(),
                seccion == null ? null : seccion.getId(),
                seccion == null ? null : seccion.getClave(),
                media.getTipo(),
                media.getUrl(),
                media.getPublicId(),
                media.getAlt(),
                media.getAncho(),
                media.getAlto(),
                media.getOrden(),
                media.isActiva());
    }
}
