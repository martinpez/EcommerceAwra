package com.awra.co.ecommerceawra.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.awra.co.ecommerceawra.config.CloudinaryProperties;
import com.awra.co.ecommerceawra.exception.ServicioExternoException;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

@Component
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

    /** Transformacion del poster: primer frame del video, en jpg y calidad automatica. */
    private static final String TRANSFORMACION_POSTER = "so_0,f_jpg,q_auto";

    /** La misma transformacion, expresada con la API del SDK. */
    private static final Transformation POSTER = new Transformation()
            .startOffset("0")
            .fetchFormat("jpg")
            .quality("auto");

    /** Transformacion de los videos: bitrate bajo, H.264, 720 px y 24 fps. */
    private static final String TRANSFORMACION_VIDEO = "q_45,vc_h264,w_720,fps_24";

    /** Ancho de entrega de las imagenes de producto y de las secciones. */
    public static final int ANCHO_ENTREGA_IMAGEN = 1200;

    /** Ancho de entrega del logo: se muestra enorme en el hero. */
    public static final int ANCHO_ENTREGA_LOGO = 1600;

    private final Cloudinary cloudinary;
    private final CloudinaryProperties properties;

    public CloudinaryService(Cloudinary cloudinary, CloudinaryProperties properties) {
        this.cloudinary = cloudinary;
        this.properties = properties;
    }

    public ImagenSubida subir(byte[] contenido) {
        return subir(contenido, properties.carpeta(), ANCHO_ENTREGA_IMAGEN);
    }

    /**
     * Sube una imagen en la carpeta indicada.
     *
     * La URL que se devuelve no es la original, sino la de entrega: lleva las
     * transformaciones {@code f_auto} (WebP o AVIF segun el navegador) y
     * {@code q_auto} (calidad ajustada al contenido). Guardar la original
     * multiplicaria por diez el peso de cada foto.
     */
    public ImagenSubida subir(byte[] contenido, String carpeta, int anchoEntrega) {
        try {
            Map<?, ?> resultado = cloudinary.uploader().upload(contenido,
                    ObjectUtils.asMap("folder", carpeta, "resource_type", "image"));
            String publicId = (String) resultado.get("public_id");
            return new ImagenSubida(
                    urlDeEntregaImagen(publicId, anchoEntrega),
                    publicId,
                    aEntero(resultado.get("width")),
                    aEntero(resultado.get("height")));
        } catch (Exception ex) {
            log.error("Fallo al subir la imagen a Cloudinary: {}", ex.getMessage(), ex);
            throw new ServicioExternoException("No se pudo subir la imagen al almacenamiento externo", ex);
        }
    }

    /**
     * Sube un video y prepara su poster (el primer frame).
     *
     * El poster se genera con la transformacion {@code so_0}: es el fotograma
     * del segundo 0. El resto de transformaciones las aplica la URL de
     * entrega, que es donde el navegador decide que formato y calidad usar.
     */
    public VideoSubido subirVideo(byte[] contenido, String carpeta) {
        try {
            // El SDK de Cloudinary 2.0.0 espera "eager" como una lista de
            // objetos Transformation. Pasarlo como cadena falla con
            // ClassCastException al castear a List y luego a Transformation.
            Map<String, Object> opciones = new HashMap<>();
            opciones.put("folder", carpeta);
            opciones.put("resource_type", "video");
            opciones.put("format", "mp4");
            opciones.put("eager", List.of(POSTER));

            Map<?, ?> resultado = cloudinary.uploader().upload(contenido, opciones);
            String publicId = (String) resultado.get("public_id");
            return new VideoSubido(
                    urlDeEntregaVideo(publicId),
                    publicId,
                    urlPoster(publicId),
                    aEntero(resultado.get("width")),
                    aEntero(resultado.get("height")),
                    aEntero(resultado.get("duration")));
        } catch (Exception ex) {
            log.error("Fallo al subir el video a Cloudinary: {}", ex.getMessage(), ex);
            throw new ServicioExternoException("No se pudo subir el video al almacenamiento externo", ex);
        }
    }

    public void eliminarSilencioso(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        } catch (Exception ex) {
            log.error("No se pudo borrar en Cloudinary la imagen huérfana {}", publicId, ex);
        }
    }

    public void eliminarVideoSilencioso(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
        } catch (Exception ex) {
            log.error("No se pudo borrar en Cloudinary el video huérfano {}", publicId, ex);
        }
    }

    /** URL optimizada de una imagen a partir de su public_id. */
    public String urlDeEntregaImagen(String publicId, int ancho) {
        return "https://res.cloudinary.com/" + properties.cloudName()
                + "/image/upload/f_auto,q_auto,w_" + ancho + "/" + publicId;
    }

    /** URL optimizada de un video a partir de su public_id. */
    public String urlDeEntregaVideo(String publicId) {
        return "https://res.cloudinary.com/" + properties.cloudName()
                + "/video/upload/" + TRANSFORMACION_VIDEO + "/" + publicId + ".mp4";
    }

    /** URL del poster de un video a partir de su public_id. */
    public String urlPoster(String publicId) {
        return "https://res.cloudinary.com/" + properties.cloudName()
                + "/video/upload/" + TRANSFORMACION_POSTER + "/" + publicId + ".jpg";
    }

    private static Integer aEntero(Object valor) {
        if (valor instanceof Number numero) {
            return numero.intValue();
        }
        if (valor instanceof String texto && !texto.isBlank()) {
            return (int) Double.parseDouble(texto);
        }
        return null;
    }

    public record ImagenSubida(String url, String publicId, Integer ancho, Integer alto) {
    }

    public record VideoSubido(String url, String publicId, String posterUrl, Integer ancho, Integer alto,
            Integer duracionSeg) {
    }
}
