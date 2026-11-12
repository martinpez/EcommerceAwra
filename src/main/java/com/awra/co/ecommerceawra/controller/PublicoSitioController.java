package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awra.co.ecommerceawra.dto.EdicionPublicaResponse;
import com.awra.co.ecommerceawra.dto.SitioPublicoResponse;
import com.awra.co.ecommerceawra.service.SitioPublicoService;

@Tag(name = "Público - Sitio")
@RestController
@RequestMapping("/api/publico")
public class PublicoSitioController {

    private final SitioPublicoService sitioPublicoService;

    public PublicoSitioController(SitioPublicoService sitioPublicoService) {
        this.sitioPublicoService = sitioPublicoService;
    }

    /** Empresa, secciones, medios y videos activos. */
    @GetMapping("/sitio")
    public SitioPublicoResponse sitio() {
        return sitioPublicoService.sitio();
    }

    /** Ediciones de la Home: solo las activas con el producto publicado. */
    @GetMapping("/ediciones")
    public List<EdicionPublicaResponse> ediciones() {
        return sitioPublicoService.ediciones();
    }
}
