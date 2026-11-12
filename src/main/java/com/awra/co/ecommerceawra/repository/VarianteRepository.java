package com.awra.co.ecommerceawra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awra.co.ecommerceawra.entity.VarianteEntity;

public interface VarianteRepository extends JpaRepository<VarianteEntity, Long> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    List<VarianteEntity> findByProducto_Id(Long productoId);
}
