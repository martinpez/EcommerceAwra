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
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.entity.VarianteEntity;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles({"dev", "test"})
class ProductoRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VarianteRepository varianteRepository;

    @Autowired
    private EntityManager entityManager;

    private ProductoEntity producto;
    private VarianteEntity varianteM;
    private VarianteEntity varianteL;

    @BeforeEach
    void datos() {
        CategoriaEntity categoria = categoriaRepository.save(new CategoriaEntity("Buzos"));
        producto = productoRepository.save(new ProductoEntity("Buzo A", null, new BigDecimal("40000"), categoria));

        varianteM = new VarianteEntity(producto, "M", "Negro", "A-M", 1);
        varianteM.modificarStock(5);
        varianteL = new VarianteEntity(producto, "L", "Negro", "A-L", 1);
        varianteL.modificarStock(3);
        varianteRepository.saveAll(List.of(varianteM, varianteL));
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void productoEliminadoNoSeEncuentraNiSeLista() {
        productoRepository.delete(productoRepository.findById(producto.getId()).orElseThrow());
        entityManager.flush();
        entityManager.clear();

        assertThat(productoRepository.findById(producto.getId())).isEmpty();
        assertThat(productoRepository.findAll()).isEmpty();
    }

    @Test
    void elBorradoConservaLaFilaEnLaBaseDeDatos() {
        Long id = producto.getId();
        productoRepository.delete(productoRepository.findById(id).orElseThrow());
        entityManager.flush();
        entityManager.clear();

        Number filas = (Number) entityManager.createNativeQuery("select count(*) from producto where id = :id")
                .setParameter("id", id)
                .getSingleResult();
        assertThat(filas.longValue()).isEqualTo(1L);
    }

    @Test
    void varianteEliminadaNoOcupaSkuNiAportaStockAlInventario() {
        varianteRepository.delete(varianteL);
        entityManager.flush();
        entityManager.clear();

        assertThat(varianteRepository.existsBySku("A-L")).isFalse();
        var pagina = productoRepository.buscarInventario(PageRequest.of(0, 10));
        assertThat(pagina.getContent()).hasSize(1);
        assertThat(pagina.getContent().get(0).stockTotal()).isEqualTo(5L);
    }

    @Test
    void sinVariantesEliminadasElInventarioSumaTodoElStock() {
        var pagina = productoRepository.buscarInventario(PageRequest.of(0, 10));

        assertThat(pagina.getContent().get(0).stockTotal()).isEqualTo(8L);
    }
}
