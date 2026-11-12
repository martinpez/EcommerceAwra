package com.awra.co.ecommerceawra.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.awra.co.ecommerceawra.config.CloudinaryProperties;
import com.awra.co.ecommerceawra.dto.VideoSitioRequest;
import com.awra.co.ecommerceawra.dto.VideoSitioResponse;
import com.awra.co.ecommerceawra.entity.SeccionSitioEntity;
import com.awra.co.ecommerceawra.entity.VideoSitioEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.media.ValidacionArchivo;
import com.awra.co.ecommerceawra.repository.VideoSitioRepository;

/**
 * Videos del sitio (la seccion "En movimiento").
 *
 * Cada video guarda su poster (primer frame) para que la seccion no se vea
 * en negro mientras descarga.
 */
@Service
public class VideoSitioService {

    /** Clave de la seccion donde caen los videos si no se indica ninguna. */
    private static final String SECCION_POR_DEFECTO = "MOVIMIENTO";

    private final VideoSitioRepository videoRepository;
    private final SeccionSitioService seccionSitioService;
    private final CloudinaryService cloudinaryService;
    private final CloudinaryProperties cloudinaryProperties;
    private final TransactionTemplate transactionTemplate;

    public VideoSitioService(VideoSitioRepository videoRepository,
            SeccionSitioService seccionSitioService,
            CloudinaryService cloudinaryService,
            CloudinaryProperties cloudinaryProperties,
            TransactionTemplate transactionTemplate) {
        this.videoRepository = videoRepository;
        this.seccionSitioService = seccionSitioService;
        this.cloudinaryService = cloudinaryService;
        this.cloudinaryProperties = cloudinaryProperties;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional(readOnly = true)
    public List<VideoSitioResponse> listar() {
        return videoRepository.findAll().stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VideoSitioResponse> listarPorSeccion(String seccionClave) {
        SeccionSitioEntity seccion = seccionSitioService.buscarPorClave(seccionClave);
        return videoRepository.findBySeccion_IdAndActivaTrueOrderByOrdenAsc(seccion.getId()).stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VideoSitioResponse> listarActivos() {
        return videoRepository.findByActivaTrueOrderByOrdenAsc().stream()
                .map(this::aResponse)
                .toList();
    }

    public VideoSitioResponse subir(MultipartFile archivo, VideoSitioRequest request) {
        byte[] contenido = ValidacionArchivo.leerVideo(archivo);
        SeccionSitioEntity seccion = resolverSeccion(request.seccionClave());

        CloudinaryService.VideoSubido subida = cloudinaryService.subirVideo(contenido, cloudinaryProperties.carpetaSitio());
        try {
            return transactionTemplate.execute(estado -> guardar(seccion, request, subida));
        } catch (RuntimeException ex) {
            cloudinaryService.eliminarVideoSilencioso(subida.publicId());
            throw ex;
        }
    }

    @Transactional
    public VideoSitioResponse actualizar(Long id, VideoSitioRequest request) {
        VideoSitioEntity video = buscarEntidad(id);
        video.setSeccion(resolverSeccion(request.seccionClave()));
        video.setTitulo(request.titulo().trim());
        video.setEtiqueta(request.etiqueta());
        video.setOrden(request.orden());
        video.setActiva(request.activa());
        return aResponse(video);
    }

    @Transactional
    public void eliminar(Long id) {
        VideoSitioEntity video = buscarEntidad(id);
        String publicId = video.getPublicId();
        videoRepository.delete(video);
        videoRepository.flush();
        cloudinaryService.eliminarVideoSilencioso(publicId);
    }

    private VideoSitioResponse guardar(SeccionSitioEntity seccion, VideoSitioRequest request,
            CloudinaryService.VideoSubido subida) {
        VideoSitioEntity video = videoRepository.save(new VideoSitioEntity(
                seccion,
                request.titulo().trim(),
                request.etiqueta(),
                subida.url(),
                subida.publicId(),
                subida.posterUrl(),
                subida.ancho(),
                subida.alto(),
                subida.duracionSeg(),
                request.orden()));
        video.setActiva(request.activa());
        return aResponse(video);
    }

    private SeccionSitioEntity resolverSeccion(String seccionClave) {
        String clave = seccionClave == null || seccionClave.isBlank() ? SECCION_POR_DEFECTO : seccionClave.trim();
        return seccionSitioService.buscarPorClave(clave);
    }

    private VideoSitioEntity buscarEntidad(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Video", id));
    }

    private VideoSitioResponse aResponse(VideoSitioEntity video) {
        SeccionSitioEntity seccion = video.getSeccion();
        return new VideoSitioResponse(
                video.getId(),
                seccion == null ? null : seccion.getId(),
                seccion == null ? null : seccion.getClave(),
                video.getTitulo(),
                video.getEtiqueta(),
                video.getUrl(),
                video.getPublicId(),
                video.getPosterUrl(),
                video.getAncho(),
                video.getAlto(),
                video.getDuracionSeg(),
                video.getOrden(),
                video.isActiva());
    }
}
