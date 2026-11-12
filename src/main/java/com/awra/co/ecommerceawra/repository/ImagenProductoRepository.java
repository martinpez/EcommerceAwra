package com.awra.co.ecommerceawra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.awra.co.ecommerceawra.entity.ImagenProductoEntity;

public interface ImagenProductoRepository extends JpaRepository<ImagenProductoEntity, Long> {

    List<ImagenProductoEntity> findByProducto_IdOrderByOrdenAsc(Long productoId);

    /**
     * Imagenes de varios productos en una sola consulta.
     *
     * Se usa para pintar una pagina del catalogo: una consulta para la pagina
     * y otra para todas sus fotos, en vez de una por producto.
     */
    @Query("""
            select i from ImagenProductoEntity i
            where i.producto.id in :productoIds
            order by i.orden asc
            """)
    List<ImagenProductoEntity> findByProducto_IdInOrderByOrdenAsc(@Param("productoIds") List<Long> productoIds);
}
