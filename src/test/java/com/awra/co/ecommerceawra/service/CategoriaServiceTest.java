package com.awra.co.ecommerceawra.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.awra.co.ecommerceawra.dto.CategoriaRequest;
import com.awra.co.ecommerceawra.entity.CategoriaEntity;
import com.awra.co.ecommerceawra.exception.ReglaNegocioException;
import com.awra.co.ecommerceawra.repository.CategoriaRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void crearConNombreDuplicadoLanzaReglaDeNegocio() {
        when(categoriaRepository.existsByNombre("Buzos")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.crear(new CategoriaRequest("Buzos")))
                .isInstanceOf(ReglaNegocioException.class);
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void eliminarCategoriaConProductosActivosNoBorra() {
        CategoriaEntity categoria = new CategoriaEntity("Buzos");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.existsByCategoria_Id(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.eliminar(1L))
                .isInstanceOf(ReglaNegocioException.class);
        verify(categoriaRepository, never()).delete(any());
    }

    @Test
    void eliminarCategoriaSinProductosLaBorra() {
        CategoriaEntity categoria = new CategoriaEntity("Buzos");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.existsByCategoria_Id(1L)).thenReturn(false);

        categoriaService.eliminar(1L);

        verify(categoriaRepository).delete(categoria);
    }
}
