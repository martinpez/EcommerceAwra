package com.awra.co.ecommerceawra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.awra.co.ecommerceawra.dto.ProductoResponse;
import com.awra.co.ecommerceawra.dto.PublicacionRequest;
import com.awra.co.ecommerceawra.entity.CategoriaEntity;
import com.awra.co.ecommerceawra.entity.EstadoPublicacion;
import com.awra.co.ecommerceawra.entity.ImagenProductoEntity;
import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.entity.VarianteEntity;
import com.awra.co.ecommerceawra.exception.ReglaNegocioException;
import com.awra.co.ecommerceawra.repository.CategoriaRepository;
import com.awra.co.ecommerceawra.repository.EdicionRepository;
import com.awra.co.ecommerceawra.repository.ImagenProductoRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;
import com.awra.co.ecommerceawra.repository.VarianteRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private VarianteRepository varianteRepository;

    @Mock
    private ImagenProductoRepository imagenRepository;

    @Mock
    private EdicionRepository edicionRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void noSePuedePublicarUnProductoSinImagen() {
        ProductoEntity producto = producto("Buzo Señal Nocturna");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(varianteRepository.findByProducto_Id(1L)).thenReturn(List.of(variante(producto, "M")));

        assertThatThrownBy(() -> productoService.cambiarPublicacion(1L,
                new PublicacionRequest(EstadoPublicacion.PUBLICADO)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("sin al menos una imagen");
    }

    @Test
    void noSePuedePublicarUnProductoSinTalla() {
        ProductoEntity producto = producto("Buzo Señal Nocturna");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(varianteRepository.findByProducto_Id(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> productoService.cambiarPublicacion(1L,
                new PublicacionRequest(EstadoPublicacion.PUBLICADO)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("sin al menos una talla");
    }

    @Test
    void unProductoCompletoSePublica() {
        ProductoEntity producto = producto("Buzo Señal Nocturna");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(varianteRepository.findByProducto_Id(1L)).thenReturn(List.of(variante(producto, "M")));
        when(imagenRepository.findByProducto_IdOrderByOrdenAsc(1L)).thenReturn(List.of(imagen(producto)));

        ProductoResponse respuesta = productoService.cambiarPublicacion(1L,
                new PublicacionRequest(EstadoPublicacion.PUBLICADO));

        assertThat(respuesta.estadoPublicacion()).isEqualTo(EstadoPublicacion.PUBLICADO);
    }

    @Test
    void alEliminarUnProductoSusEdicionesSeDesactivan() {
        ProductoEntity producto = producto("Buzo Señal Nocturna");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(varianteRepository.findByProducto_Id(1L)).thenReturn(List.of(variante(producto, "M")));
        when(imagenRepository.findByProducto_IdOrderByOrdenAsc(1L)).thenReturn(List.of(imagen(producto)));

        productoService.eliminar(1L);

        verify(edicionRepository).desactivarPorProducto(1L);
    }

    private ProductoEntity producto(String nombre) {
        return new ProductoEntity(nombre, "Descripcion", new BigDecimal("189900"), new CategoriaEntity("Buzos"));
    }

    private VarianteEntity variante(ProductoEntity producto, String talla) {
        return new VarianteEntity(producto, talla, "Negro", "SKU-" + talla, 2);
    }

    private ImagenProductoEntity imagen(ProductoEntity producto) {
        return new ImagenProductoEntity(producto, "https://x/a.webp", "carpeta/a", 1, true);
    }
}
