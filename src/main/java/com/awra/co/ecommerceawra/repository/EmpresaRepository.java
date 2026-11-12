package com.awra.co.ecommerceawra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awra.co.ecommerceawra.entity.EmpresaEntity;

public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Long> {

    /** La tabla solo admite una fila (ID = 1); este metodo la recupera. */
    Optional<EmpresaEntity> findFirstByOrderByIdAsc();
}
