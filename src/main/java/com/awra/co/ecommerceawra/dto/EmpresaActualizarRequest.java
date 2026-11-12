package com.awra.co.ecommerceawra.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmpresaActualizarRequest(
        @NotBlank(message = "El nombre de la empresa es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        String nombre,

        @Size(max = 150, message = "El slogan no puede superar 150 caracteres")
        String slogan,

        @Size(max = 600, message = "La descripción no puede superar 600 caracteres")
        String descripcion,

        @NotBlank(message = "El número de WhatsApp es obligatorio")
        @Pattern(regexp = "\\d{7,20}", message = "El WhatsApp debe tener entre 7 y 20 dígitos, sin signos ni espacios")
        String whatsapp,

        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String telefono,

        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres")
        String email,

        @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
        String direccion,

        @Size(max = 80, message = "La ciudad no puede superar 80 caracteres")
        String ciudad,

        @Size(max = 80, message = "El país no puede superar 80 caracteres")
        String pais,

        @Size(max = 120, message = "Instagram no puede superar 120 caracteres")
        String instagram,

        @Size(max = 120, message = "TikTok no puede superar 120 caracteres")
        String tiktok,

        @Size(max = 120, message = "Facebook no puede superar 120 caracteres")
        String facebook,

        @Size(max = 120, message = "YouTube no puede superar 120 caracteres")
        String youtube,

        @Size(max = 150, message = "El horario no puede superar 150 caracteres")
        String horario) {
}
