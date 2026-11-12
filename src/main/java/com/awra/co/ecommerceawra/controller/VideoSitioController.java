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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.awra.co.ecommerceawra.dto.VideoSitioRequest;
import com.awra.co.ecommerceawra.dto.VideoSitioResponse;
import com.awra.co.ecommerceawra.service.VideoSitioService;

import jakarta.validation.Valid;

@Tag(name = "Admin - Videos del sitio")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/sitio/videos")
public class VideoSitioController {

    private final VideoSitioService videoSitioService;

    public VideoSitioController(VideoSitioService videoSitioService) {
        this.videoSitioService = videoSitioService;
    }

    @GetMapping
    public List<VideoSitioResponse> listar() {
        return videoSitioService.listar();
    }

    @GetMapping("/seccion/{seccionClave}")
    public List<VideoSitioResponse> listarPorSeccion(@PathVariable String seccionClave) {
        return videoSitioService.listarPorSeccion(seccionClave);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public VideoSitioResponse subir(@RequestPart("archivo") MultipartFile archivo,
            @Valid @RequestPart("datos") VideoSitioRequest request) {
        return videoSitioService.subir(archivo, request);
    }

    @PutMapping("/{id}")
    public VideoSitioResponse actualizar(@PathVariable Long id, @Valid @RequestBody VideoSitioRequest request) {
        return videoSitioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        videoSitioService.eliminar(id);
    }
}
