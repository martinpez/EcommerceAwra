package com.awra.co.ecommerceawra.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        List<CampoError> errores) {
}
