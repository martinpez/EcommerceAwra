package com.awra.co.ecommerceawra.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.SeccionSitioRequest;
import com.awra.co.ecommerceawra.dto.SeccionSitioResponse;
import com.awra.co.ecommerceawra.entity.SeccionSitioEntity;
import com.awra.co.ecommerceawra.exception.RecursoNoEncontradoException;
import com.awra.co.ecommerceawra.exception.ReglaNegocioException;
import com.awra.co.ecommerceawra.repository.SeccionSitioRepository;

/**
 * Secciones del sitio: hero, quienes somos, ediciones, movimiento y contacto.
 *
 * Las secciones no se borran: se desactivan. Asi el contenido queda guardado
 * por si el administrador quiere volver a activarlo.
 */
@Service
public class SeccionSitioService {

    private final SeccionSitioRepository seccionRepository;

    public SeccionSitioService(SeccionSitioRepository seccionRepository) {
        this.seccionRepository = seccionRepository;
    }

    @Transactional(readOnly = true)
    public List<SeccionSitioResponse> listar() {
        return seccionRepository.findAllByOrderByOrdenAsc().stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeccionSitioResponse> listarActivas() {
        return seccionRepository.findByActivaTrueOrderByOrdenAsc().stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeccionSitioResponse obtener(Long id) {
        return aResponse(buscarEntidad(id));
    }

    @Transactional
    public SeccionSitioResponse crear(SeccionSitioRequest request) {
        String clave = request.clave().trim();
        if (seccionRepository.existsByClave(clave)) {
            throw new ReglaNegocioException("Ya existe una sección con la clave '" + clave + "'");
        }
        SeccionSitioEntity seccion = new SeccionSitioEntity(
                clave,
                request.eyebrow(),
                request.titulo(),
                request.cuerpo(),
                request.orden());
        seccion.setActiva(request.activa());
        return aResponse(seccionRepository.save(seccion));
    }

    @Transactional
    public SeccionSitioResponse actualizar(Long id, SeccionSitioRequest request) {
        SeccionSitioEntity seccion = buscarEntidad(id);
        String clave = request.clave().trim();
        if (seccionRepository.existsByClaveAndIdNot(clave, id)) {
            throw new ReglaNegocioException("Ya existe una sección con la clave '" + clave + "'");
        }
        seccion.setEyebrow(request.eyebrow());
        seccion.setTitulo(request.titulo());
        seccion.setCuerpo(request.cuerpo());
        seccion.setOrden(request.orden());
        seccion.setActiva(request.activa());
        return aResponse(seccion);
    }

    /** Busca una seccion por su clave. La usan los medios y los videos. */
    @Transactional(readOnly = true)
    public SeccionSitioEntity buscarPorClave(String clave) {
        return seccionRepository.findByClave(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sección", clave));
    }

    private SeccionSitioEntity buscarEntidad(Long id) {
        return seccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sección", id));
    }

    private SeccionSitioResponse aResponse(SeccionSitioEntity seccion) {
        return new SeccionSitioResponse(
                seccion.getId(),
                seccion.getClave(),
                seccion.getEyebrow(),
                seccion.getTitulo(),
                seccion.getCuerpo(),
                seccion.getOrden(),
                seccion.isActiva());
    }
}
