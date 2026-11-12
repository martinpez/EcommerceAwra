package com.awra.co.ecommerceawra.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.awra.co.ecommerceawra.dto.ImagenResponse;
import com.awra.co.ecommerceawra.dto.OrdenImagenRequest;
import com.awra.co.ecommerceawra.service.ImagenService;

import jakarta.validation.Valid;

@Tag(name = "Admin - Imágenes de producto")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/admin/productos/{productoId}/imagenes")
public class ImagenController {

    private final ImagenService imagenService;

    public ImagenController(ImagenService imagenService) {
        this.imagenService = imagenService;
    }

    @GetMapping
    public List<ImagenResponse> listar(@PathVariable Long productoId) {
        return imagenService.listar(productoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ImagenResponse subir(@PathVariable Long productoId, @RequestParam("archivo") MultipartFile archivo) {
        return imagenService.subir(productoId, archivo);
    }

    @PatchMapping("/{imagenId}/principal")
    public ImagenResponse marcarPrincipal(@PathVariable Long productoId, @PathVariable Long imagenId) {
        return imagenService.marcarPrincipal(productoId, imagenId);
    }

    @PatchMapping("/{imagenId}/orden")
    public ImagenResponse actualizarOrden(@PathVariable Long productoId, @PathVariable Long imagenId,
            @Valid @RequestBody OrdenImagenRequest request) {
        return imagenService.actualizarOrden(productoId, imagenId, request.orden());
    }

    @DeleteMapping("/{imagenId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long productoId, @PathVariable Long imagenId) {
        imagenService.eliminar(productoId, imagenId);
    }
}
