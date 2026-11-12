package com.awra.co.ecommerceawra.content;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.entity.SeccionSitioEntity;
import com.awra.co.ecommerceawra.entity.TipoMedia;
import com.awra.co.ecommerceawra.repository.MediaSitioRepository;
import com.awra.co.ecommerceawra.repository.SeccionSitioRepository;
import com.awra.co.ecommerceawra.repository.VideoSitioRepository;
import com.awra.co.ecommerceawra.entity.MediaSitioEntity;
import com.awra.co.ecommerceawra.entity.VideoSitioEntity;
import com.awra.co.ecommerceawra.service.CloudinaryService;

/**
 * Contenido inicial del sitio.
 *
 * Solo inserta lo que falta: si el administrador ya cambio los textos, el
 * runner no los pisa. Los identificadores de Cloudinary apuntan a los
 * archivos que el frontend subio con su script, de modo que la web se ve
 * igual al pasar de los datos fijos a la API.
 */
@Component
public class ContenidoInicialRunner implements CommandLineRunner {

    /** Carpeta de Cloudinary donde el frontend subio los medios. */
    private static final String CARPETA_FRONTEND = "ecommerce-awra/frontend";

    private final SeccionSitioRepository seccionRepository;
    private final MediaSitioRepository mediaRepository;
    private final VideoSitioRepository videoRepository;
    private final CloudinaryService cloudinaryService;

    public ContenidoInicialRunner(SeccionSitioRepository seccionRepository,
            MediaSitioRepository mediaRepository,
            VideoSitioRepository videoRepository,
            CloudinaryService cloudinaryService) {
        this.seccionRepository = seccionRepository;
        this.mediaRepository = mediaRepository;
        this.videoRepository = videoRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Si ya hay secciones, la base de datos no esta vacia: el administrador
        // ya tiene el contenido y no se debe pisar nada. Asi, borrar el logo
        // desde el panel no lo resucita al reiniciar el servidor.
        if (seccionRepository.count() > 0) {
            return;
        }
        crearSecciones();
        crearLogo();
        crearVideos();
    }

    private void crearSecciones() {
        crearSeccionSiFalta("HERO", "Design in Colombia", null, null, 1);
        crearSeccionSiFalta("QUIENES_SOMOS", "Design in Colombia",
                "Vestir la noche también es una forma de moverse.",
                "AWRA nace entre calles húmedas, motores y luces que aparecen por un segundo. Diseñamos piezas "
                        + "para quienes no necesitan llamar la atención para hacerse visibles.",
                2);
        crearSeccionSiFalta("EDICIONES", "Colección", null, null, 3);
        crearSeccionSiFalta("MOVIMIENTO", "Archivo audiovisual", "AWRA en movimiento.",
                "La ropa cambia cuando sale del estudio y entra en la calle.", 4);
        crearSeccionSiFalta("CONTACTO", "Contacto", "Nos vemos después del último semáforo.",
                "Pide tu edición por WhatsApp o síguenos cuando caiga la noche.", 5);
    }

    private void crearSeccionSiFalta(String clave, String eyebrow, String titulo, String cuerpo, int orden) {
        if (seccionRepository.findByClave(clave).isPresent()) {
            return;
        }
        seccionRepository.save(new SeccionSitioEntity(clave, eyebrow, titulo, cuerpo, orden));
    }

    private void crearLogo() {
        String publicId = CARPETA_FRONTEND + "/brand/awra-logo";
        SeccionSitioEntity hero = seccionRepository.findByClave("HERO").orElse(null);
        mediaRepository.save(new MediaSitioEntity(
                hero,
                TipoMedia.LOGO,
                cloudinaryService.urlDeEntregaImagen(publicId, CloudinaryService.ANCHO_ENTREGA_LOGO),
                publicId,
                "Logotipo de AWRA",
                1600,
                533,
                1));
    }

    private void crearVideos() {
        SeccionSitioEntity movimiento = seccionRepository.findByClave("MOVIMIENTO").orElse(null);
        String[] titulos = {
                "Night study 01",
                "Night study 02",
                "Night study 03",
                "Night study 04"
        };
        for (int indice = 0; indice < titulos.length; indice++) {
            String publicId = CARPETA_FRONTEND + "/motion/night-study-" + String.format("%02d", indice + 1);
            videoRepository.save(new VideoSitioEntity(
                    movimiento,
                    titulos[indice],
                    titulos[indice],
                    cloudinaryService.urlDeEntregaVideo(publicId),
                    publicId,
                    cloudinaryService.urlPoster(publicId),
                    720,
                    1280,
                    null,
                    indice + 1));
        }
    }
}
