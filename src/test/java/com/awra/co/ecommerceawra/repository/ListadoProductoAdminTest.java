package com.awra.co.ecommerceawra.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.awra.co.ecommerceawra.entity.CategoriaEntity;
import com.awra.co.ecommerceawra.entity.EstadoPublicacion;
import com.awra.co.ecommerceawra.entity.ImagenProductoEntity;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.entity.VarianteEntity;

import jakarta.persistence.EntityManager;

/**
 * Pruebas del listado con filtros del panel.
 *
 * Lo importante aqui es que los tres filtros son opcionales: cuando llegan a
 * null la consulta no debe filtrar nada, y cuando llegan con valor debe
 * acotar. Es el caso que mas falla en JPQL con parametros nulos.
 */
@DataJpaTest
@ActiveProfiles({"dev", "test"})
class ListadoProductoAdminTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VarianteRepository varianteRepository;

    @Autowired
    private ImagenProductoRepository imagenRepository;

    @Autowired
    private EntityManager entityManager;

    private CategoriaEntity buzos;
    private CategoriaEntity chaquetas;

    @BeforeEach
    void datos() {
        buzos = categoriaRepository.save(new CategoriaEntity("Buzos"));
        chaquetas = categoriaRepository.save(new CategoriaEntity("Chaquetas"));

        crear("Buzo Señal Nocturna", buzos, EstadoPublicacion.PUBLICADO, 4);
        crear("Buzo Línea de Fuga", buzos, EstadoPublicacion.PUBLICADO, 0);
        crear("Buzo en borrador", buzos, EstadoPublicacion.BORRADOR, 2);
        crear("Chaqueta de cuero", chaquetas, EstadoPublicacion.OCULTO, 1);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void sinFiltrosSalenTodosLosProductos() {
        var pagina = productoRepository.buscarParaAdmin(null, null, null, PageRequest.of(0, 20));

        assertThat(pagina.getTotalElements()).isEqualTo(4);
    }

    @Test
    void elFiltroPorEstadoDejaSoloLosProductosEseEstado() {
        var pagina = productoRepository.buscarParaAdmin(EstadoPublicacion.PUBLICADO, null, null,
                PageRequest.of(0, 20));

        assertThat(pagina.getTotalElements()).isEqualTo(2);
        assertThat(pagina.getContent()).allMatch(p -> p.estadoPublicacion() == EstadoPublicacion.PUBLICADO);
    }

    @Test
    void elFiltroPorCategoriaDejaSoloLosDeEsaCategoria() {
        var pagina = productoRepository.buscarParaAdmin(null, buzos.getId(), null, PageRequest.of(0, 20));

        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getContent()).allMatch(p -> p.categoriaId().equals(buzos.getId()));
    }

    @Test
    void laBusquedaPorNombreNoDistingueMayusculasNiMinusculas() {
        var pagina = productoRepository.buscarParaAdmin(null, null, "BUZO", PageRequest.of(0, 20));

        assertThat(pagina.getTotalElements()).isEqualTo(3);
    }

    @Test
    void laBusquedaRespetaLosAcentosDelNombre() {
        // "Linea" sin tilde no encuentra "Línea": la busqueda es simple, sin
        // normalizar acentos. Es una decision consciente para no depender de
        // funciones propias de cada base de datos.
        var pagina = productoRepository.buscarParaAdmin(null, null, "linea", PageRequest.of(0, 20));

        assertThat(pagina.getTotalElements()).isZero();
    }

    @Test
    void losFiltrosSeCombinan() {
        var pagina = productoRepository.buscarParaAdmin(EstadoPublicacion.PUBLICADO, buzos.getId(), "fuga",
                PageRequest.of(0, 20));

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).nombre()).isEqualTo("Buzo Línea de Fuga");
    }

    @Test
    void elListadoCalculaElStockTotalDeCadaProducto() {
        var pagina = productoRepository.buscarParaAdmin(null, null, null, PageRequest.of(0, 20));

        assertThat(pagina.getContent())
                .filteredOn(p -> p.nombre().equals("Buzo Señal Nocturna"))
                .singleElement()
                .satisfies(p -> assertThat(p.stockTotal()).isEqualTo(4L));
    }

    @Test
    void laImagenPrincipalSeEligeConUnaSolaConsultaParaTodaLaPagina() {
        ProductoEntity producto = productoRepository.findAll().stream()
                .filter(p -> p.getNombre().equals("Buzo Señal Nocturna"))
                .findFirst()
                .orElseThrow();
        imagenRepository.save(new ImagenProductoEntity(producto, "https://x/2.webp", "id/2", 2, false));
        imagenRepository.save(new ImagenProductoEntity(producto, "https://x/1.webp", "id/1", 1, true));
        entityManager.flush();
        entityManager.clear();

        List<ImagenProductoEntity> imagenes = imagenRepository
                .findByProducto_IdInOrderByOrdenAsc(List.of(producto.getId()));

        assertThat(imagenes).hasSize(2);
        assertThat(imagenes.get(0).getUrl()).isEqualTo("https://x/1.webp");
        assertThat(imagenes.get(0).isEsPrincipal()).isTrue();
    }

    private void crear(String nombre, CategoriaEntity categoria, EstadoPublicacion estado, int stock) {
        ProductoEntity producto = new ProductoEntity(nombre, "Descripcion", new BigDecimal("189900"), categoria);
        producto.setEstadoPublicacion(estado);
        producto = productoRepository.save(producto);

        VarianteEntity variante = new VarianteEntity(producto, "M", "Negro", "SKU-" + nombre, 1);
        variante.modificarStock(stock);
        varianteRepository.save(variante);
    }
}
