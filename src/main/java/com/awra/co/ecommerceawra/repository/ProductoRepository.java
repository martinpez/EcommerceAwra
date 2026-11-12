package com.awra.co.ecommerceawra.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.awra.co.ecommerceawra.dto.InventarioProductoResumen;
import com.awra.co.ecommerceawra.dto.ProductoAdminResumen;
import com.awra.co.ecommerceawra.dto.ProductoPublicadoResumen;
import com.awra.co.ecommerceawra.entity.EstadoPublicacion;
import com.awra.co.ecommerceawra.entity.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

    List<ProductoEntity> findByCategoria_Id(Long categoriaId);

    boolean existsByCategoria_Id(Long categoriaId);

    @Query(value = """
            select new com.awra.co.ecommerceawra.dto.InventarioProductoResumen(
                p.id, p.nombre, c.id, c.nombre, coalesce(sum(v.stock), 0))
            from ProductoEntity p
            join p.categoria c
            left join VarianteEntity v on v.producto = p
            group by p.id, p.nombre, c.id, c.nombre
            """,
            countQuery = """
            select count(p) from ProductoEntity p
            """)
    Page<InventarioProductoResumen> buscarInventario(Pageable pageable);

    @Query(value = """
            select new com.awra.co.ecommerceawra.dto.ProductoPublicadoResumen(
                p.id, p.nombre, p.precio, c.id, c.nombre, coalesce(sum(v.stock), 0))
            from ProductoEntity p
            join p.categoria c
            left join VarianteEntity v on v.producto = p
            where p.estadoPublicacion = com.awra.co.ecommerceawra.entity.EstadoPublicacion.PUBLICADO
            group by p.id, p.nombre, p.precio, c.id, c.nombre
            """,
            countQuery = """
            select count(p) from ProductoEntity p
            where p.estadoPublicacion = com.awra.co.ecommerceawra.entity.EstadoPublicacion.PUBLICADO
            """)
    Page<ProductoPublicadoResumen> buscarPublicados(Pageable pageable);

    @Query(value = """
            select new com.awra.co.ecommerceawra.dto.ProductoPublicadoResumen(
                p.id, p.nombre, p.precio, c.id, c.nombre, coalesce(sum(v.stock), 0))
            from ProductoEntity p
            join p.categoria c
            left join VarianteEntity v on v.producto = p
            where p.estadoPublicacion = com.awra.co.ecommerceawra.entity.EstadoPublicacion.PUBLICADO
              and c.id = :categoriaId
            group by p.id, p.nombre, p.precio, c.id, c.nombre
            """,
            countQuery = """
            select count(p) from ProductoEntity p
            join p.categoria c
            where p.estadoPublicacion = com.awra.co.ecommerceawra.entity.EstadoPublicacion.PUBLICADO
              and c.id = :categoriaId
            """)
    Page<ProductoPublicadoResumen> buscarPublicadosPorCategoria(@Param("categoriaId") Long categoriaId,
            Pageable pageable);

    /**
     * Listado del panel, con filtros opcionales.
     *
     * Los tres filtros son opcionales: si llegan a null, la condicion se
     * cumple siempre y no acota el resultado. Por eso se comparan con
     * {@code is null} y no con un valor por defecto.
     */
    @Query(value = """
            select new com.awra.co.ecommerceawra.dto.ProductoAdminResumen(
                p.id, p.nombre, p.precio, c.id, c.nombre, p.estadoPublicacion, coalesce(sum(v.stock), 0), p.fechaCreacion)
            from ProductoEntity p
            join p.categoria c
            left join VarianteEntity v on v.producto = p
            where (:estado is null or p.estadoPublicacion = :estado)
              and (:categoriaId is null or c.id = :categoriaId)
              and (:busqueda is null or lower(p.nombre) like lower(concat('%', :busqueda, '%')))
            group by p.id, p.nombre, p.precio, c.id, c.nombre, p.estadoPublicacion, p.fechaCreacion
            """,
            countQuery = """
            select count(p) from ProductoEntity p
            join p.categoria c
            where (:estado is null or p.estadoPublicacion = :estado)
              and (:categoriaId is null or c.id = :categoriaId)
              and (:busqueda is null or lower(p.nombre) like lower(concat('%', :busqueda, '%')))
            """)
    Page<ProductoAdminResumen> buscarParaAdmin(@Param("estado") EstadoPublicacion estado,
            @Param("categoriaId") Long categoriaId, @Param("busqueda") String busqueda, Pageable pageable);
}
