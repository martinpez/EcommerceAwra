package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.awra.co.ecommerceawra.dto.MediaSitioRequest;
import com.awra.co.ecommerceawra.dto.MediaSitioResponse;
import com.awra.co.ecommerceawra.service.MediaSitioService;

import jakarta.validation.Valid;

@Tag(name = "Admin - Medios del sitio")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/sitio/media")
public class MediaSitioController {

    private final MediaSitioService mediaSitioService;

    public MediaSitioController(MediaSitioService mediaSitioService) {
        this.mediaSitioService = mediaSitioService;
    }

    @GetMapping
    public List<MediaSitioResponse> listar() {
        return mediaSitioService.listar();
    }

    @GetMapping("/seccion/{seccionClave}")
    public List<MediaSitioResponse> listarPorSeccion(@PathVariable String seccionClave) {
        return mediaSitioService.listarPorSeccion(seccionClave);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MediaSitioResponse subir(@RequestPart("archivo") MultipartFile archivo,
            @Valid @RequestPart("datos") MediaSitioRequest request) {
        return mediaSitioService.subir(archivo, request);
    }

    @PutMapping("/{id}")
    public MediaSitioResponse actualizar(@PathVariable Long id, @Valid @RequestBody MediaSitioRequest request) {
        return mediaSitioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        mediaSitioService.eliminar(id);
    }
}
