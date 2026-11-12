package com.awra.co.ecommerceawra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.awra.co.ecommerceawra.dto.EdicionResponse;
import com.awra.co.ecommerceawra.entity.CategoriaEntity;
import com.awra.co.ecommerceawra.entity.EdicionEntity;
import com.awra.co.ecommerceawra.entity.EstadoPublicacion;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.repository.EdicionRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class EdicionServiceTest {

    @Mock
    private EdicionRepository edicionRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private EdicionService edicionService;

    @Test
    void unaEdicionDeUnProductoPublicadoEsVisible() {
        ProductoEntity producto = producto(EstadoPublicacion.PUBLICADO);
        EdicionEntity edicion = new EdicionEntity(producto, "Edicion I", "AWRA", "Descripcion", 1);
        edicion.setActiva(true);
        when(edicionRepository.findTodasConProducto()).thenReturn(List.of(edicion));

        List<EdicionResponse> ediciones = edicionService.listar();

        assertThat(ediciones).hasSize(1);
        assertThat(ediciones.get(0).visible()).isTrue();
        assertThat(ediciones.get(0).productoEstado()).isEqualTo(EstadoPublicacion.PUBLICADO);
    }

    @Test
    void unaEdicionDeUnProductoEnBorradorNoEsVisible() {
        ProductoEntity producto = producto(EstadoPublicacion.BORRADOR);
        EdicionEntity edicion = new EdicionEntity(producto, "Edicion I", "AWRA", "Descripcion", 1);
        edicion.setActiva(true);
        when(edicionRepository.findTodasConProducto()).thenReturn(List.of(edicion));

        List<EdicionResponse> ediciones = edicionService.listar();

        assertThat(ediciones.get(0).activa()).isTrue();
        assertThat(ediciones.get(0).visible()).isFalse();
    }

    @Test
    void unaEdicionDesactivadaNoEsVisible() {
        ProductoEntity producto = producto(EstadoPublicacion.PUBLICADO);
        EdicionEntity edicion = new EdicionEntity(producto, "Edicion I", "AWRA", "Descripcion", 1);
        edicion.setActiva(false);
        when(edicionRepository.findTodasConProducto()).thenReturn(List.of(edicion));

        assertThat(edicionService.listar().get(0).visible()).isFalse();
    }

    private ProductoEntity producto(EstadoPublicacion estado) {
        ProductoEntity producto = new ProductoEntity("Buzo", "Descripcion", new BigDecimal("189900"),
                new CategoriaEntity("Buzos"));
        producto.setEstadoPublicacion(estado);
        return producto;
    }
}
