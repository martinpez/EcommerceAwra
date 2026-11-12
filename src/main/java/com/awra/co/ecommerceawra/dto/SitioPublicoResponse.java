package com.awra.co.ecommerceawra.dto;

import java.util.List;

/**
 * Todo lo que la web necesita del sitio en una sola respuesta.
 *
 * Agruparlo evita que el navegador haga cinco peticiones seguidas: la Home
 * carga, y con una llamada ya tiene empresa, textos, medios y videos.
 */
public record SitioPublicoResponse(
        EmpresaResponse empresa,
        List<SeccionSitioResponse> secciones,
        List<MediaSitioResponse> media,
        List<VideoSitioResponse> videos) {
}
