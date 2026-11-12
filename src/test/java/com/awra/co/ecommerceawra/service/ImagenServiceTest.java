package com.awra.co.ecommerceawra.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionTemplate;

import com.awra.co.ecommerceawra.entity.ProductoEntity;
import com.awra.co.ecommerceawra.exception.ArchivoNoValidoException;
import com.awra.co.ecommerceawra.repository.ImagenProductoRepository;
import com.awra.co.ecommerceawra.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ImagenServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ImagenProductoRepository imagenRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private ImagenService imagenService;

    @Test
    void archivoQueNoEsUnaImagenSeRechazaAntesDeSubirlo() {
        MockMultipartFile falso = new MockMultipartFile("archivo", "foto.jpg", "image/jpeg",
                "esto es texto, no una imagen".getBytes());

        assertThatThrownBy(() -> imagenService.subir(1L, falso))
                .isInstanceOf(ArchivoNoValidoException.class);
        verify(cloudinaryService, never()).subir(any());
    }

    @Test
    void siFallaElGuardadoTrasLaSubidaSeBorraLaImagenDeCloudinary() {
        MockMultipartFile png = new MockMultipartFile("archivo", "foto.png", "image/png", firmaPng());
        ProductoEntity producto = new ProductoEntity("Buzo", null, BigDecimal.TEN, null);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(cloudinaryService.subir(any())).thenReturn(new CloudinaryService.ImagenSubida("https://x/a.png", "carpeta/a", null, null));
        when(transactionTemplate.execute(any())).thenThrow(new RuntimeException("base de datos caida"));

        assertThatThrownBy(() -> imagenService.subir(1L, png))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("base de datos caida");
        verify(cloudinaryService).eliminarSilencioso("carpeta/a");
    }

    private static byte[] firmaPng() {
        return new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
    }
}
