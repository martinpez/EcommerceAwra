package com.awra.co.ecommerceawra.content;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.awra.co.ecommerceawra.dto.MovimientoRequest;
import com.awra.co.ecommerceawra.entity.CategoriaEntity;
import com.awra.co.ecommerceawra.entity.EdicionEntity;
import com.awra.co.ecommerceawra.entity.EstadoPublicacion;
import com.awra.co.ecommerceawra.entity.ImagenProductoEntity;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.entity.TipoMovimiento;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.repository.CategoriaRepository;
import com.awra.co.ecommerceawra.repository.EdicionRepository;
import com.awra.co.ecommerceawra.repository.ImagenProductoRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;
import com.awra.co.ecommerceawra.repository.VarianteRepository;
import com.awra.co.ecommerceawra.service.CloudinaryService;
import com.awra.co.ecommerceawra.service.MovimientoService;

/**
 * Datos de demostracion para desarrollo.
 *
 * Crea dos productos con sus tallas, stock, fotos y ediciones, de forma que
 * la web se pueda developar contra la API sin montar el catalogo a mano.
 * En produccion no corre: ahi el catalodo lo carga el administrador.
 */
@Component
@Profile("dev")
public class DatosDeDemostracionRunner implements CommandLineRunner {

    private static final String CARPETA_FRONTEND = "ecommerce-awra/frontend";

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final VarianteRepository varianteRepository;
    private final ImagenProductoRepository imagenRepository;
    private final EdicionRepository edicionRepository;
    private final MovimientoService movimientoService;
    private final CloudinaryService cloudinaryService;

    public DatosDeDemostracionRunner(CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            VarianteRepository varianteRepository,
            ImagenProductoRepository imagenRepository,
            EdicionRepository edicionRepository,
            MovimientoService movimientoService,
            CloudinaryService cloudinaryService) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.varianteRepository = varianteRepository;
        this.imagenRepository = imagenRepository;
        this.edicionRepository = edicionRepository;
        this.movimientoService = movimientoService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (productoRepository.count() > 0) {
            return;
        }
        CategoriaEntity buzos = categoriaRepository.findAll().stream()
                .filter(categoria -> "Buzos".equalsIgnoreCase(categoria.getNombre()))
                .findFirst()
                .orElseGet(() -> categoriaRepository.save(new CategoriaEntity("Buzos")));

        ProductoEntity primero = crearProducto(
                "Buzo Señal Nocturna",
                "Buzo urbano de silueta amplia con detalles reflectivos.",
                new BigDecimal("189900"),
                buzos,
                List.of(new Talla("S", "Negro", "AWRA-SN-S", 4), new Talla("M", "Negro", "AWRA-SN-M", 2),
                        new Talla("L", "Negro", "AWRA-SN-L", 0)),
                List.of("catalogo/edicion-i/frente", "catalogo/edicion-i/espalda"));

        ProductoEntity segundo = crearProducto(
                "Buzo Línea de Fuga",
                "Pieza de edición limitada para rodar después del último semáforo.",
                new BigDecimal("219900"),
                buzos,
                List.of(new Talla("M", "Grafito", "AWRA-LF-M", 0), new Talla("L", "Grafito", "AWRA-LF-L", 0)),
                List.of("catalogo/edicion-ii/frente", "catalogo/edicion-ii/espalda"));

        crearEdicion(primero, "Edición I", "AWRA",
                "La primera señal de la noche. Una silueta negra, símbolos afilados y una presencia que aparece "
                        + "cuando baja la luz.",
                1);
        crearEdicion(segundo, "Edición II", "THE LOVERS",
                "Dos fuerzas, una misma ruta. Una pieza gótica construida para moverse entre lo íntimo y lo visible.",
                2);
    }

    private ProductoEntity crearProducto(String nombre, String descripcion, BigDecimal precio,
            CategoriaEntity categoria, List<Talla> tallas, List<String> imagenes) {
        ProductoEntity producto = new ProductoEntity(nombre, descripcion, precio, categoria);
        producto.setEstadoPublicacion(EstadoPublicacion.PUBLICADO);
        producto = productoRepository.save(producto);

        for (int indice = 0; indice < tallas.size(); indice++) {
            Talla talla = tallas.get(indice);
            VarianteEntity variante = varianteRepository.save(new VarianteEntity(
                    producto, talla.talla(), talla.color(), talla.sku(), 2));
            if (talla.stock() > 0) {
                movimientoService.registrar(variante.getId(),
                        new MovimientoRequest(TipoMovimiento.ENTRADA, talla.stock(), "Stock inicial de demostracion"));
            }
        }

        for (int indice = 0; indice < imagenes.size(); indice++) {
            String publicId = CARPETA_FRONTEND + "/" + imagenes.get(indice);
            imagenRepository.save(new ImagenProductoEntity(
                    producto,
                    cloudinaryService.urlDeEntregaImagen(publicId, CloudinaryService.ANCHO_ENTREGA_IMAGEN),
                    publicId,
                    indice + 1,
                    indice == 0));
        }
        return producto;
    }

    private void crearEdicion(ProductoEntity producto, String eyebrow, String titulo, String descripcion, int orden) {
        EdicionEntity edicion = new EdicionEntity(producto, eyebrow, titulo, descripcion, orden);
        edicion.setActiva(true);
        edicionRepository.save(edicion);
    }

    private record Talla(String talla, String color, String sku, int stock) {
    }
}
