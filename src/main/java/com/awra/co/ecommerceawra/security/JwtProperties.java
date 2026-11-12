package com.awra.co.ecommerceawra.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank(message = "JWT_SECRET es obligatorio")
        @Size(min = 32, message = "JWT_SECRET debe tener al menos 32 caracteres")
        String secret,

        @Positive
        long expiracionMinutos) {
}
