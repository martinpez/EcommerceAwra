package com.awra.co.ecommerceawra.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no existe");
    }

    /** Para los recursos que se buscan por una clave y no por un id. */
    public RecursoNoEncontradoException(String recurso, String clave) {
        super(recurso + " '" + clave + "' no existe");
    }
}
