package com.awra.co.ecommerceawra.dto;

import java.util.List;

/**
 * Quien esta autenticado.
 *
 * El panel lo llama al abrirse para comprobar que el token sigue siendo
 * valido y para mostrar el nombre del administrador.
 */
public record AuthMeResponse(
        String username,
        List<String> roles) {
}
