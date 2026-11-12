package com.awra.co.ecommerceawra.exception;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<CampoError> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new CampoError(error.getField(), error.getDefaultMessage()))
                .toList();
        return respuesta(HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos", request, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> cuerpoIllegible(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido", request, List.of());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> reglaNegocio(ReglaNegocioException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violación de integridad en {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return respuesta(HttpStatus.CONFLICT,
                "La operación entra en conflicto con datos relacionados", request, List.of());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> conflictoConcurrencia(ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT,
                "El registro fue modificado por otra operación. Vuelve a intentarlo", request, List.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> autenticacion(AuthenticationException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos", request, List.of());
    }

    @ExceptionHandler(ArchivoNoValidoException.class)
    public ResponseEntity<ApiError> archivoNoValido(ArchivoNoValidoException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> archivoDemasiadoGrande(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.PAYLOAD_TOO_LARGE, "La imagen supera el tamaño máximo de 5 MB", request, List.of());
    }

    @ExceptionHandler({MultipartException.class, MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ApiError> campoFaltante(Exception ex, HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "Falta un campo obligatorio en la petición", request, List.of());
    }

    @ExceptionHandler(ServicioExternoException.class)
    public ResponseEntity<ApiError> servicioExterno(ServicioExternoException ex, HttpServletRequest request) {
        log.error("Fallo en servicio externo en {}", request.getRequestURI(), ex);
        return respuesta(HttpStatus.BAD_GATEWAY, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> inesperado(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {}", request.getRequestURI(), ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request, List.of());
    }

    private ResponseEntity<ApiError> respuesta(HttpStatus status, String mensaje,
            HttpServletRequest request, List<CampoError> errores) {
        ApiError cuerpo = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                errores);
        return ResponseEntity.status(status).body(cuerpo);
    }
}
