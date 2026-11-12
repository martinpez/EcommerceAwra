package com.awra.co.ecommerceawra.media;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.awra.co.ecommerceawra.exception.ArchivoNoValidoException;

/**
 * Validacion de archivos por firma binaria.
 *
 * No se confia en el nombre ni en el content-type que envia el navegador:
 * se miran los primeros bytes del archivo. Un archivo que dice ser .jpg pero
 * empieza con "PK" (un ZIP) es un archivo peligroso y se rechaza.
 */
public final class ValidacionArchivo {

    public static final long TAMANO_MAXIMO_IMAGEN_BYTES = 5L * 1024 * 1024;
    public static final long TAMANO_MAXIMO_VIDEO_BYTES = 50L * 1024 * 1024;

    private ValidacionArchivo() {
    }

    /** Lee y valida una imagen (jpg, png o webp) de hasta 5 MB. */
    public static byte[] leerImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoNoValidoException("Debes enviar una imagen en el campo 'archivo'");
        }
        if (archivo.getSize() > TAMANO_MAXIMO_IMAGEN_BYTES) {
            throw new ArchivoNoValidoException("La imagen supera el tamaño máximo de 5 MB");
        }
        byte[] contenido = leer(archivo);
        if (!esFormatoPermitido(contenido)) {
            throw new ArchivoNoValidoException("Formato no permitido: solo se aceptan jpg, png o webp");
        }
        return contenido;
    }

    /** Lee y valida un video (mp4 o webm) de hasta 50 MB. */
    public static byte[] leerVideo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoNoValidoException("Debes enviar un video en el campo 'archivo'");
        }
        if (archivo.getSize() > TAMANO_MAXIMO_VIDEO_BYTES) {
            throw new ArchivoNoValidoException("El video supera el tamaño máximo de 50 MB");
        }
        byte[] contenido = leer(archivo);
        if (!esVideoPermitido(contenido)) {
            throw new ArchivoNoValidoException("Formato no permitido: solo se aceptan mp4 o webm");
        }
        return contenido;
    }

    private static byte[] leer(MultipartFile archivo) {
        try {
            return archivo.getBytes();
        } catch (IOException ex) {
            throw new ArchivoNoValidoException("No se pudo leer el archivo enviado");
        }
    }

    public static boolean esFormatoPermitido(byte[] contenido) {
        return esJpeg(contenido) || esPng(contenido) || esWebp(contenido);
    }

    public static boolean esVideoPermitido(byte[] contenido) {
        return esMp4(contenido) || esWebm(contenido);
    }

    public static boolean esJpeg(byte[] contenido) {
        return contenido.length >= 3
                && (contenido[0] & 0xFF) == 0xFF
                && (contenido[1] & 0xFF) == 0xD8
                && (contenido[2] & 0xFF) == 0xFF;
    }

    public static boolean esPng(byte[] contenido) {
        return empiezaCon(contenido, 0, "\u0089PNG\r\n\u001a\n");
    }

    public static boolean esWebp(byte[] contenido) {
        return empiezaCon(contenido, 0, "RIFF") && empiezaCon(contenido, 8, "WEBP");
    }

    /** Los archivos mp4 tienen la marca "ftyp" en los bytes 4 a 7. */
    public static boolean esMp4(byte[] contenido) {
        return empiezaCon(contenido, 4, "ftyp");
    }

    /** Los archivos webm son RIFF con la marca "WEBM" en los bytes 8 a 11. */
    public static boolean esWebm(byte[] contenido) {
        return empiezaCon(contenido, 0, "RIFF") && empiezaCon(contenido, 8, "WEBM");
    }

    private static boolean empiezaCon(byte[] contenido, int desplazamiento, String firma) {
        if (contenido.length < desplazamiento + firma.length()) {
            return false;
        }
        for (int i = 0; i < firma.length(); i++) {
            if (contenido[desplazamiento + i] != (byte) firma.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
