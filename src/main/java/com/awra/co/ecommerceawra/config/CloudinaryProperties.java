package com.awra.co.ecommerceawra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "cloudinary")
public record CloudinaryProperties(
        @NotBlank(message = "CLOUDINARY_CLOUD_NAME es obligatorio") String cloudName,
        @NotBlank(message = "CLOUDINARY_API_KEY es obligatorio") String apiKey,
        @NotBlank(message = "CLOUDINARY_API_SECRET es obligatorio") String apiSecret,
        @NotBlank String carpeta,
        /** Carpeta del contenido del sitio: logo, imagenes de secciones y videos. */
        @NotBlank String carpetaSitio) {
}
