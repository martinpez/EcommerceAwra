package com.awra.co.ecommerceawra.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.awra.co.ecommerceawra.entity.EdicionEntity;

public interface EdicionRepository extends JpaRepository<EdicionEntity, Long> {

    List<EdicionEntity> findByProducto_Id(Long productoId);

    List<EdicionEntity> findByProducto_IdAndActivaTrue(Long productoId);

    List<EdicionEntity> findAllByOrderByOrdenAsc();

    /**
     * Todas las ediciones con su producto.
     *
     * El join es obligatorio: {@code ProductoEntity} filtra los productos
     * dados de baja, asi que una edicion cuyo producto fue eliminado deja de
     * tener producto que leer. Con el join, esas ediciones huerfanas se
     * descartan solas en vez de reventar la consulta.
     */
    @Query("""
            select e from EdicionEntity e
            join fetch e.producto p
            order by e.orden asc
            """)
    List<EdicionEntity> findTodasConProducto();

    @Query("""
            select e from EdicionEntity e
            join fetch e.producto p
            where e.id = :id
            """)
    Optional<EdicionEntity> findByIdConProducto(@Param("id") Long id);

    /**
     * Ediciones que la web puede mostrar: activas y con el producto publicado.
     *
     * El join con el producto hace el filtro en la base de datos, no en
     * memoria: asi un producto en borrador u oculto desaparece de la Home
     * aunque su edicion siga activa.
     */
    @Query("""
            select e from EdicionEntity e
            join fetch e.producto p
            where e.activa = true
              and p.estadoPublicacion = com.awra.co.ecommerceawra.entity.EstadoPublicacion.PUBLICADO
            order by e.orden asc
            """)
    List<EdicionEntity> findPublicas();

    /**
     * Desactiva las ediciones de un producto con una sola sentencia.
     *
     * Se hace con una actualizacion masiva y no cargando las ediciones una a
     * una: si se cargaran, quedarian "sucias" en el contexto de persistencia
     * y al borrar el producto Hibernate las marcaria como transientes, con el
     * error TransientPropertyValueException.
     */
    @Modifying(flushAutomatically = true)
    @Query("""
            update EdicionEntity e
            set e.activa = false, e.fechaActualizacion = CURRENT_TIMESTAMP
            where e.producto.id = :productoId
            """)
    int desactivarPorProducto(@Param("productoId") Long productoId);
}
