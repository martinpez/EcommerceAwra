package com.awra.co.ecommerceawra.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.awra.co.ecommerceawra.entity.AlertaStockEntity;

public interface AlertaStockRepository extends JpaRepository<AlertaStockEntity, Long> {

    Optional<AlertaStockEntity> findFirstByVariante_IdAndFechaResolucionIsNull(Long varianteId);

    @Query("""
            select a from AlertaStockEntity a
            join fetch a.variante v
            join fetch v.producto
            order by a.fechaCreacion desc, a.id desc
            """)
    List<AlertaStockEntity> buscarVisibles();

    @Query("""
            select a from AlertaStockEntity a
            join fetch a.variante v
            join fetch v.producto
            where a.leida = :leida
            order by a.fechaCreacion desc, a.id desc
            """)
    List<AlertaStockEntity> buscarVisiblesPorLeida(@Param("leida") boolean leida);

    @Query("""
            select a from AlertaStockEntity a
            join fetch a.variante v
            join fetch v.producto
            where a.id = :id
            """)
    Optional<AlertaStockEntity> buscarVisiblePorId(@Param("id") Long id);
}
