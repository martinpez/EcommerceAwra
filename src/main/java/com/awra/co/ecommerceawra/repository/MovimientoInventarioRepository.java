package com.awra.co.ecommerceawra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awra.co.ecommerceawra.entity.MovimientoInventarioEntity;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventarioEntity, Long> {

    List<MovimientoInventarioEntity> findByVariante_IdOrderByFechaMovimientoDescIdDesc(Long varianteId);
}
